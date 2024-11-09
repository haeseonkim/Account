package com.wev.account.domain.timezone.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class AccountTimezoneWebDTO {

    private AccountTimezoneWebDTO() {
        throw new IllegalStateException("Dto group class");
    }


    @Getter
    public static class UpdateWebReq {
        private String timezone;
        @NotNull
        private boolean isDeferred;

        public AccountTimezoneServiceDTO.UpdateDTO toServiceDto(Long accountId) {
            return AccountTimezoneMapper.INSTANCE.toServiceDto(this, accountId);
        }
    }

    @Getter
    @Builder
    public static class GetWebRes {
        private Long accountId;
        private String timezone;
        private ZonedDateTime updateDeferredUntil;
    }
}
