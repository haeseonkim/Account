package com.wev.account.domain.timezone;

import com.wev.account.domain.timezone.model.AccountTimezone;
import com.wev.account.domain.timezone.model.AccountTimezoneDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTimezoneRepository extends JpaRepository<AccountTimezone, Long> {
    Optional<AccountTimezoneDTO> findByAccountId(Long accountId);
}
