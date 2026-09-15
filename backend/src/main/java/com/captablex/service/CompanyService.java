package com.captablex.service;

import com.captablex.domain.CompanyProfile;
import com.captablex.dto.request.CreateCompanyRequest;
import com.captablex.dto.response.CompanyResponse;
import com.captablex.exception.ResourceNotFoundException;
import com.captablex.mapper.CapTableMapper;
import com.captablex.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyProfileRepository companyProfileRepository;
    private final CapTableMapper mapper;

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        CompanyProfile company = CompanyProfile.builder()
                .companyName(request.getCompanyName().trim())
                .currentValuation(request.getCurrentValuation())
                .build();
        CompanyProfile saved = companyProfileRepository.save(company);
        return mapper.toCompanyResponse(saved);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompany(UUID companyId) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        return mapper.toCompanyResponse(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompanies() {
        return companyProfileRepository.findAll().stream()
                .map(mapper::toCompanyResponse)
                .collect(Collectors.toList());
    }
}
