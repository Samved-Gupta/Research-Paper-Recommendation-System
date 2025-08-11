package com.recommender.paper_recommender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_time", nullable = false, updatable = false)
    private LocalDateTime loginTime;

    @PrePersist
    protected void onCreate() {
        loginTime = LocalDateTime.now();
    }

    public LoginHistory(User user) {
        this.user = user;
    }
}
