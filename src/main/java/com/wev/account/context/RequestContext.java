package com.wev.account.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Setter
@Getter
@Component
@RequestScope
public class RequestContext {
    private Long accountId;
    private String timezone;
}
