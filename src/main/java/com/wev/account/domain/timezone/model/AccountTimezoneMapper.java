package com.wev.account.domain.timezone.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountTimezoneMapper {
    AccountTimezoneMapper INSTANCE = Mappers.getMapper(AccountTimezoneMapper.class);

    AccountTimezoneWebDTO.GetWebRes toWebDto(AccountTimezone accountTimezone);

    AccountTimezoneServiceDTO.UpdateDTO toServiceDto(AccountTimezoneWebDTO.UpdateWebReq updateWebReq, Long accountId);
}