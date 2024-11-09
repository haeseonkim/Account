package com.wev.account.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class AccountTimezoneWebDTO {

    private AccountTimezoneWebDTO() {
        throw new IllegalStateException("Dto group class");
    }

    @Getter
    @Builder
    public static class GetWebRes {
        private Long accountId;
        private String timezone;
        private ZonedDateTime updateDeferredUntil;
    }
}
