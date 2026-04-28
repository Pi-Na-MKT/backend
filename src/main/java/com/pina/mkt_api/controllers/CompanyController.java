package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.CompanyDTOs.CompanyRequestDTO;
import com.pina.mkt_api.dtos.CompanyDTOs.CompanyResponseDTO;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
@Tag(name = "Companies", description = "Gerenciamento de empresas clientes")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar empresa", description = "Cadastra uma nova empresa no sistema")
    public ResponseEntity<CompanyResponseDTO> create(@RequestBody CompanyRequestDTO requestDTO) {
        Company company = new Company();
        company.setName(requestDTO.name());
        company.setSlug(requestDTO.slug());
        company.setActive(requestDTO.active() != null ? requestDTO.active() : true);

        Company savedCompany = service.create(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedCompany));
    }

    @GetMapping
    @Operation(summary = "Listar empresas", description = "Retorna todas as empresas cadastradas")
    public ResponseEntity<List<CompanyResponseDTO>> getAll() {
        List<CompanyResponseDTO> response = service.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID", description = "Retorna os detalhes de uma empresa específica")
    public ResponseEntity<CompanyResponseDTO> getById(@PathVariable Long id) {
        Company company = service.findById(id);
        return ResponseEntity.ok(toDTO(company));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa", description = "Atualiza os dados de uma empresa existente")
    public ResponseEntity<CompanyResponseDTO> update(@PathVariable Long id, @RequestBody CompanyRequestDTO requestDTO) {
        Company company = new Company();
        company.setName(requestDTO.name());
        company.setSlug(requestDTO.slug());
        company.setActive(requestDTO.active());

        Company updatedCompany = service.update(id, company);
        return ResponseEntity.ok(toDTO(updatedCompany));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir empresa", description = "Remove uma empresa pelo seu ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CompanyResponseDTO toDTO(Company company) {
        return new CompanyResponseDTO(
                company.getId(),
                company.getName(),
                company.getSlug(),
                company.getActive(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}