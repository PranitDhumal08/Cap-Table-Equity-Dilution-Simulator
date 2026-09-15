package com.captablex.repository;

import com.captablex.domain.FundingRoundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FundingRoundTransactionRepository extends JpaRepository<FundingRoundTransaction, UUID> {

    List<FundingRoundTransaction> findByCompanyCompanyIdOrderByExecutedAtDesc(UUID companyId);
}
