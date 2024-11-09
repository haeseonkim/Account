package com.wev.account.domain.timezone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNT_TIMEZONE")
public class AccountTimezone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    // 타임존 ID (America/New_York, Asia/Seoul, Asia/Tokyo ...)
    private String timezone;

    // 타임존 업데이트 유보 시간
    @Column(nullable = true)
    private ZonedDateTime updateDeferredUntil;

    public void updateTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setUpdateDeferredUntil1Month() {
        this.updateDeferredUntil = ZonedDateTime.now().plusMonths(1); // 1개월 유보 시간 설정
    }
}
