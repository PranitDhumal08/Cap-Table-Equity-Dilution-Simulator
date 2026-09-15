package com.captablex.service;

import com.captablex.domain.CapTableLedger;
import com.captablex.domain.CompanyProfile;
import com.captablex.domain.Stakeholder;
import com.captablex.dto.request.AddLedgerEntryRequest;
import com.captablex.dto.request.CreateStakeholderRequest;
import com.captablex.dto.response.LedgerResponse;
import com.captablex.dto.response.StakeholderResponse;
import com.captablex.exception.ResourceNotFoundException;
import com.captablex.mapper.CapTableMapper;
import com.captablex.repository.CapTableLedgerRepository;
import com.captablex.repository.CompanyProfileRepository;
import com.captablex.repository.StakeholderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StakeholderService {

    private final CompanyProfileRepository companyProfileRepository;
    private final StakeholderRepository stakeholderRepository;
    private final CapTableLedgerRepository capTableLedgerRepository;
    private final CapTableMapper mapper;

    @Transactional
    public StakeholderResponse createStakeholder(UUID companyId, CreateStakeholderRequest request) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot add stakeholder. Company not found with ID: " + companyId));

        Stakeholder stakeholder = Stakeholder.builder()
                .company(company)
                .name(request.getName().trim())
                .role(request.getRole())
                .build();

        Stakeholder saved = stakeholderRepository.save(stakeholder);
        return mapper.toStakeholderResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StakeholderResponse> getStakeholdersByCompany(UUID companyId) {
        if (!companyProfileRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }
        return stakeholderRepository.findByCompanyCompanyId(companyId).stream()
                .map(mapper::toStakeholderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LedgerResponse addLedgerEntry(UUID stakeholderId, AddLedgerEntryRequest request) {
        Stakeholder stakeholder = stakeholderRepository.findById(stakeholderId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot add share ledger entry. Stakeholder not found with ID: " + stakeholderId));

        CapTableLedger ledger = CapTableLedger.builder()
                .stakeholder(stakeholder)
                .shareClass(request.getShareClass())
                .sharesOwned(request.getSharesOwned())
                .build();

        CapTableLedger saved = capTableLedgerRepository.save(ledger);
        return mapper.toLedgerResponse(saved);
    }
}
