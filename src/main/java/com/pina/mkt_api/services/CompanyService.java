package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.CompanyDTOs.CompanyRequestDTO;
import com.pina.mkt_api.dtos.CompanyDTOs.CompanyUpdateDTO;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository repository;
    private final SecurityUtils securityUtils;

    public CompanyService(CompanyRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    public Company create(CompanyRequestDTO dto) {
        if (repository.existsBySlug(dto.slug())) {
            throw new BusinessRuleException("Já existe uma empresa cadastrada com este slug.");
        }

        Company company = new Company();
        company.setName(dto.name());
        company.setSlug(dto.slug());
        company.setIsActive(dto.active() != null ? dto.active() : true);

        return repository.save(company);
    }

    public List<Company> findAll() {
        if (securityUtils.isAdmin()) {
            return repository.findAll();
        }
        return repository.findAccessibleByUserEmail(securityUtils.getAuthenticatedEmail());
    }

    public Company findById(Long id) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (!securityUtils.isAdmin() &&
                repository.countBoardsInCompanyForUser(id, securityUtils.getAuthenticatedEmail()) == 0) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }

        return company;
    }

    public Company update(Long id, CompanyUpdateDTO dto) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        if (dto.slug() != null && !dto.slug().isBlank() && !company.getSlug().equals(dto.slug())) {
            if (repository.existsBySlug(dto.slug())) {
                throw new BusinessRuleException("Já existe outra empresa utilizando este slug.");
            }
            company.setSlug(dto.slug());
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            company.setName(dto.name());
        }

        if (dto.active() != null) {
            company.setIsActive(dto.active());
        }

        return repository.save(company);
    }

    public void delete(Long id) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
        repository.delete(company);
    }
}
