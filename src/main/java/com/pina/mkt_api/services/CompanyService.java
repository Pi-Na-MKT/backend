package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public Company create(Company company) {
        if (repository.existsBySlug(company.getSlug())) {
            throw new BusinessRuleException("Já existe uma empresa cadastrada com este slug.");
        }
        return repository.save(company);
    }

    public List<Company> findAll() {
        return repository.findAll();
    }

    public Company findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
    }

    public Company update(Long id, Company dadosAtualizados) {
        Company companyExistente = findById(id);

        if (!companyExistente.getSlug().equals(dadosAtualizados.getSlug()) && repository.existsBySlug(dadosAtualizados.getSlug())) {
            throw new BusinessRuleException("Já existe outra empresa utilizando este slug.");
        }

        companyExistente.setName(dadosAtualizados.getName());
        companyExistente.setSlug(dadosAtualizados.getSlug());

        if (dadosAtualizados.getIsActive() != null) {
            companyExistente.setIsActive(dadosAtualizados.getIsActive());
        }

        return repository.save(companyExistente);
    }

    public void delete(Long id) {
        Company company = findById(id);
        repository.delete(company);
    }
}