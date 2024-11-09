package com.wev.account.domain.timezone.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class AccountTimezoneServiceDTO {

    private AccountTimezoneServiceDTO() {
        throw new IllegalStateException("Dto group class");
    }

    @Builder
    @Getter
    public static class UpdateDTO {
        private Long accountId;
        private String timezone;
        private boolean isDeferred;
    }
}
