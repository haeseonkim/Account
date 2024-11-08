package com.wev.domain.accounttimezone.repository;

import com.wev.domain.accounttimezone.model.AccountTimezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTimezoneRepository extends JpaRepository<AccountTimezone, Long> {
    Optional<AccountTimezone> findByAccountId(Long accountId);
}
