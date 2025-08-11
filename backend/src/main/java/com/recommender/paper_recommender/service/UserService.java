package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.dto.LoginHistoryDto;
import com.recommender.paper_recommender.dto.LoginRequest;
import com.recommender.paper_recommender.dto.PasswordChangeRequest;
import com.recommender.paper_recommender.dto.RegistrationRequest;
import com.recommender.paper_recommender.model.LoginHistory;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.repository.LoginHistoryRepository;
import com.recommender.paper_recommender.repository.UserRepository;
import com.recommender.paper_recommender.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.recommender.paper_recommender.model.PasswordResetToken;
import com.recommender.paper_recommender.repository.PasswordResetTokenRepository;
import org.springframework.transaction.annotation.Propagation; // <-- Import added

import java.util.Calendar;
import java.util.UUID;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    public User registerNewUser(RegistrationRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new IllegalStateException("That username is already taken. Please choose another.");
        }
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("An account with that email address already exists.");
        }
        String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        User newUser = new User(registrationRequest.getUsername(), registrationRequest.getEmail(), hashedPassword);
        return userRepository.save(newUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLoginHistory(User user) {
        loginHistoryRepository.save(new LoginHistory(user));
    }

    @Transactional
    public String loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalStateException("The email or password you entered is incorrect. Please try again."));

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // This will now run in its own transaction and commit immediately
            this.recordLoginHistory(user);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), List.of()),
                    null, List.of()
            );
            // An error is still happening here, but it won't affect the login history anymore
            return tokenProvider.generateToken(auth);
        } else {
            throw new IllegalStateException("The email or password you entered is incorrect. Please try again.");
        }
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public Page<LoginHistoryDto> getLoginHistoryForUser(Long userId, Pageable pageable) {
        Page<LoginHistory> history = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId, pageable);
        return history.map(LoginHistoryDto::new);
    }

    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    public void createPasswordResetTokenForUser(String email) {
        User user = findUserByEmail(email);
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void changeUserPassword(String token, String newPassword) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken == null) {
            throw new IllegalStateException("Invalid token.");
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalStateException("Token has expired.");
        }

        User user = passToken.getUser();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        passwordResetTokenRepository.delete(passToken);
    }
}