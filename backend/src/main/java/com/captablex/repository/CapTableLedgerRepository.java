package com.captablex.repository;

import com.captablex.domain.CapTableLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CapTableLedgerRepository extends JpaRepository<CapTableLedger, UUID> {

    List<CapTableLedger> findByStakeholderStakeholderId(UUID stakeholderId);

    @Query("SELECT l FROM CapTableLedger l " +
           "JOIN FETCH l.stakeholder s " +
           "JOIN FETCH s.company c " +
           "WHERE c.companyId = :companyId")
    List<CapTableLedger> findAllByCompanyIdWithStakeholder(@Param("companyId") UUID companyId);
}
