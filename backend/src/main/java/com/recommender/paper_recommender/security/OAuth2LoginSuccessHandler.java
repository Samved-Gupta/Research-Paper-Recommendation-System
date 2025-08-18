package com.recommender.paper_recommender.security;

import com.recommender.paper_recommender.model.LoginHistory;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.repository.LoginHistoryRepository;
import com.recommender.paper_recommender.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Random;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String name = oauth2User.getAttribute("name") != null ? oauth2User.getAttribute("name") : oauth2User.getAttribute("login");
        String email = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email") : oauth2User.getAttribute("login") + "@github.user.placeholder.com";
        final String finalEmail = email;
        final String finalName = name;

        User user = userRepository.findByEmail(finalEmail).orElseGet(() -> {
            String username = finalName;
            if (userRepository.findByUsername(finalName).isPresent()) {
                username = finalName + (new Random().nextInt(9000) + 1000);
            }
            User newUser = new User();
            newUser.setEmail(finalEmail);
            newUser.setUsername(username);
            newUser.setPassword("OAUTH2_USER");
            return userRepository.save(newUser);
        });

        loginHistoryRepository.save(new LoginHistory(user));
        String token = tokenProvider.generateToken(authentication);
        String redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}