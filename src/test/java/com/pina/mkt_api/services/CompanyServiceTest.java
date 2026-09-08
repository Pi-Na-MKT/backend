package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.CompanyDTOs.CompanyRequestDTO;
import com.pina.mkt_api.dtos.CompanyDTOs.CompanyUpdateDTO;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe CompanyService")
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CalendarIntegration calendarIntegration;

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
            CompanyRequestDTO dto = new CompanyRequestDTO("Tech Co", "tech-co", true);

            Mockito.when(companyRepository.existsBySlug("tech-co")).thenReturn(false);
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Company resultado = companyService.create(dto);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("Tech Co", resultado.getName());
            Assertions.assertEquals("tech-co", resultado.getSlug());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando slug já existe")
        void deveLancarExcecaoSlugDuplicado() {
            CompanyRequestDTO dto = new CompanyRequestDTO("Tech Co", "tech-co", true);
            Mockito.when(companyRepository.existsBySlug("tech-co")).thenReturn(true);

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
            Company empresa = buildCompany(1L, "Old Name", "old-slug");
            CompanyUpdateDTO dto = new CompanyUpdateDTO("New Name", null, null);

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Company resultado = companyService.update(1L, dto);

            Assertions.assertEquals("New Name", resultado.getName());
            Assertions.assertEquals("old-slug", resultado.getSlug());
        }

        @Test
        @DisplayName("Deve alterar o slug quando o novo slug está disponível")
        void deveAlterarSlugDisponivel() {
            Company empresa = buildCompany(1L, "Tech", "old-slug");
            CompanyUpdateDTO dto = new CompanyUpdateDTO(null, "new-slug", null);

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(companyRepository.existsBySlug("new-slug")).thenReturn(false);
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Company resultado = companyService.update(1L, dto);

            Assertions.assertEquals("new-slug", resultado.getSlug());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando o novo slug já está em uso")
        void deveLancarExcecaoSlugEmUso() {
            Company empresa = buildCompany(1L, "Tech", "old-slug");
            CompanyUpdateDTO dto = new CompanyUpdateDTO(null, "taken", null);

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(companyRepository.existsBySlug("taken")).thenReturn(true);

            Assertions.assertThrows(BusinessRuleException.class, () -> companyService.update(1L, dto));
        }

        @Test
        @DisplayName("Deve atualizar o status ativo da empresa")
        void deveAtualizarStatusAtivo() {
            Company empresa = buildCompany(1L, "Tech", "tech");
            CompanyUpdateDTO dto = new CompanyUpdateDTO(null, null, false);

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Company resultado = companyService.update(1L, dto);

            Assertions.assertFalse(resultado.getIsActive());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar empresa inexistente")
        void deveLancarExcecaoEmpresaInexistente() {
            Mockito.when(companyRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class,
                    () -> companyService.update(99L, new CompanyUpdateDTO("X", null, null)));
        }
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Admin deve listar todas as empresas")
        void adminDeveListarTodas() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(companyRepository.findAll())
                    .thenReturn(List.of(buildCompany(1L, "A", "a"), buildCompany(2L, "B", "b")));

            List<Company> resultado = companyService.findAll();

            Assertions.assertEquals(2, resultado.size());
            Mockito.verify(companyRepository).findAll();
            Mockito.verify(companyRepository, Mockito.never()).findAccessibleByUserEmail(Mockito.any());
        }

        @Test
        @DisplayName("Não-admin deve listar apenas empresas acessíveis")
        void naoAdminDeveListarAcessiveis() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("u@e.com");
            Mockito.when(companyRepository.findAccessibleByUserEmail("u@e.com"))
                    .thenReturn(List.of(buildCompany(1L, "A", "a")));

            List<Company> resultado = companyService.findAll();

            Assertions.assertEquals(1, resultado.size());
            Mockito.verify(companyRepository, Mockito.never()).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Admin deve obter qualquer empresa")
        void adminDeveObter() {
            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(buildCompany(1L, "A", "a")));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);

            Company resultado = companyService.findById(1L);

            Assertions.assertEquals("A", resultado.getName());
        }

        @Test
        @DisplayName("Não-admin com acesso deve obter a empresa")
        void naoAdminComAcessoDeveObter() {
            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(buildCompany(1L, "A", "a")));
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("u@e.com");
            Mockito.when(companyRepository.countBoardsInCompanyForUser(1L, "u@e.com")).thenReturn(1L);

            Company resultado = companyService.findById(1L);

            Assertions.assertEquals("A", resultado.getName());
        }

        @Test
        @DisplayName("Não-admin sem acesso deve receber ResourceNotFoundException")
        void naoAdminSemAcesso() {
            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(buildCompany(1L, "A", "a")));
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("u@e.com");
            Mockito.when(companyRepository.countBoardsInCompanyForUser(1L, "u@e.com")).thenReturn(0L);

            Assertions.assertThrows(ResourceNotFoundException.class, () -> companyService.findById(1L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a empresa não existe")
        void empresaNaoExiste() {
            Mockito.when(companyRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> companyService.findById(99L));
        }
    }

    @Nested
    @DisplayName("Testes do método linkGoogleCalendar")
    class LinkGoogleCalendarTests {

        @Test
        @DisplayName("Deve vincular o calendário do Google com sucesso")
        void deveVincularComSucesso() throws Exception {
            Company empresa = buildCompany(1L, "Tech Co", "tech-co");

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(calendarIntegration.createCalendarForCompany("Tech Co")).thenReturn("cal-123");
            Mockito.when(companyRepository.save(Mockito.any(Company.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Company resultado = companyService.linkGoogleCalendar(1L);

            Assertions.assertEquals("cal-123", resultado.getGoogleCalendarId());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando a empresa já possui calendário")
        void deveLancarExcecaoJaVinculado() {
            Company empresa = buildCompany(1L, "Tech Co", "tech-co");
            empresa.setGoogleCalendarId("existente");

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));

            Assertions.assertThrows(BusinessRuleException.class, () -> companyService.linkGoogleCalendar(1L));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando a integração falha")
        void deveLancarExcecaoQuandoIntegracaoFalha() throws Exception {
            Company empresa = buildCompany(1L, "Tech Co", "tech-co");

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(calendarIntegration.createCalendarForCompany("Tech Co"))
                    .thenThrow(new RuntimeException("falha na API"));

            Assertions.assertThrows(BusinessRuleException.class, () -> companyService.linkGoogleCalendar(1L));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando a integração não está configurada")
        void deveLancarExcecaoSemIntegracao() {
            CompanyService servicoSemIntegracao = new CompanyService(companyRepository, securityUtils, null);
            Company empresa = buildCompany(1L, "Tech Co", "tech-co");

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> servicoSemIntegracao.linkGoogleCalendar(1L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a empresa não existe")
        void deveLancarExcecaoEmpresaInexistente() {
            Mockito.when(companyRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> companyService.linkGoogleCalendar(99L));
        }
    }

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve excluir a empresa com sucesso")
        void deveExcluirComSucesso() {
            Company empresa = buildCompany(1L, "Tech", "tech");
            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));

            companyService.delete(1L);

            Mockito.verify(companyRepository).delete(empresa);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao excluir empresa inexistente")
        void deveLancarExcecaoAoExcluirInexistente() {
            Mockito.when(companyRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> companyService.delete(99L));
        }
    }
}
