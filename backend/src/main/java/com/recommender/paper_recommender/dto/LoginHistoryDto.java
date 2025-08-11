package com.recommender.paper_recommender.dto;

import com.recommender.paper_recommender.model.LoginHistory;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoginHistoryDto {
    private LocalDateTime loginTime;

    public LoginHistoryDto(LoginHistory loginHistory) {
        this.loginTime = loginHistory.getLoginTime();
    }
}
