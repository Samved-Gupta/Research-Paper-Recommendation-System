package com.recommender.paper_recommender.dto;

import com.recommender.paper_recommender.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private String username;
    private String email;
    private boolean isOauth2User;

    public ProfileDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        // Set the flag to true if the password is our placeholder value
        this.isOauth2User = "OAUTH2_USER".equals(user.getPassword());
    }
}