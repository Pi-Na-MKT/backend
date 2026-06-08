package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.CompanyDTOs.CompanyRequestDTO;
import com.pina.mkt_api.dtos.CompanyDTOs.CompanyUpdateDTO;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe CompanyService")
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CompanyService companyService;

    private Company buildCompany(Long id, String name, String slug) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setSlug(slug);
        company.setIsActive(true);
        return company;
    }

    @Nested
    @DisplayName("Testes do método create")
    class CreateTests {

        @Test
        @DisplayName("Deve criar empresa com sucesso")
        void deveCriarEmpresaComSucesso() {
            // arrange
            CompanyRequestDTO dto = new CompanyRequestDTO("Tech Co", "tech-co", true);

            Mockito.when(companyRepository.existsBySlug("tech-co")).thenReturn(false);
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // act
            Company resultado = companyService.create(dto);

            // assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("Tech Co", resultado.getName());
            Assertions.assertEquals("tech-co", resultado.getSlug());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando slug já existe")
        void deveLancarExcecaoSlugDuplicado() {
            // arrange
            CompanyRequestDTO dto = new CompanyRequestDTO("Tech Co", "tech-co", true);
            Mockito.when(companyRepository.existsBySlug("tech-co")).thenReturn(true);

            // act | assert
            BusinessRuleException exception = Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> companyService.create(dto)
            );
            Assertions.assertEquals("Já existe uma empresa cadastrada com este slug.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar o nome da empresa sem alterar o slug")
        void deveAtualizarNomeSemAlterarSlug() {
            // arrange
            Company empresa = buildCompany(1L, "Old Name", "old-slug");
            CompanyUpdateDTO dto = new CompanyUpdateDTO("New Name", null, null);

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // act
            Company resultado = companyService.update(1L, dto);

            // assert
            Assertions.assertEquals("New Name", resultado.getName());
            Assertions.assertEquals("old-slug", resultado.getSlug());
        }
    }
}
