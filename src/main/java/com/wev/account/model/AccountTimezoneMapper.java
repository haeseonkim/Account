package com.wev.account.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountTimezoneMapper {
    AccountTimezoneMapper INSTANCE = Mappers.getMapper(AccountTimezoneMapper.class);

    AccountTimezoneWebDTO.GetWebRes toWebDto(Account account);
}