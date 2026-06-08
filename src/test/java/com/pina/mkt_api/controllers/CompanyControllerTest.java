package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.services.CompanyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
@WithMockUser(roles = "ADMIN")
@DisplayName("Testes do CompanyController")
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;
    @MockBean
    private JwtUtil jwtUtil;

    private Company buildCompany(Long id, String name, String slug) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setSlug(slug);
        company.setIsActive(true);
        return company;
    }

    @Nested
    @DisplayName("POST /api/companies")
    class CriarTests {

        @Test
        @DisplayName("Deve criar empresa e retornar 201")
        void deveCriarEmpresa() throws Exception {
            // arrange
            Company empresa = buildCompany(1L, "Tech Co", "tech-co");
            Mockito.when(companyService.create(Mockito.any())).thenReturn(empresa);

            String corpo = """
                    {
                        "name": "Tech Co",
                        "slug": "tech-co",
                        "active": true
                    }
                    """;

            // act | assert
            mockMvc.perform(post("/api/companies")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(201))
                    .andExpect(jsonPath("$.name").value("Tech Co"))
                    .andExpect(jsonPath("$.slug").value("tech-co"));
        }
    }

    @Nested
    @DisplayName("GET /api/companies")
    class ListarTests {

        @Test
        @DisplayName("Deve listar empresas corretamente")
        void deveListarEmpresas() throws Exception {
            // arrange
            Mockito.when(companyService.findAll())
                    .thenReturn(List.of(
                            buildCompany(1L, "Tech Co", "tech-co"),
                            buildCompany(2L, "Pina MKT", "pina-mkt")
                    ));

            // act | assert
            mockMvc.perform(get("/api/companies"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$[0].name").value("Tech Co"))
                    .andExpect(jsonPath("$[1].name").value("Pina MKT"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há empresas")
        void deveRetornarListaVazia() throws Exception {
            // arrange
            Mockito.when(companyService.findAll()).thenReturn(List.of());

            // act | assert
            mockMvc.perform(get("/api/companies"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/companies/{id}")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar empresa por id corretamente")
        void deveBuscarEmpresaPorId() throws Exception {
            // arrange
            Company empresa = buildCompany(1L, "Tech Co", "tech-co");
            Mockito.when(companyService.findById(1L)).thenReturn(empresa);

            // act | assert
            mockMvc.perform(get("/api/companies/{id}", 1))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Tech Co"))
                    .andExpect(jsonPath("$.slug").value("tech-co"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando empresa não encontrada")
        void deveRetornarNotFoundQuandoNaoEncontrada() throws Exception {
            // arrange
            Mockito.when(companyService.findById(99L))
                    .thenThrow(new ResourceNotFoundException("Empresa não encontrada"));

            // act | assert
            mockMvc.perform(get("/api/companies/{id}", 99))
                    .andExpect(status().is(404));
        }
    }
}
