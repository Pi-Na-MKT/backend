package com.pina.mkt_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pina.mkt_api.dtos.UserDTOs.UserRequestDTO;
import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // ───────────────────────────────────────────────
    // TESTE: REGISTER
    // ───────────────────────────────────────────────
    @Test
    void deveRegistrarUsuario() throws Exception {

        UserRequestDTO request = new UserRequestDTO(
                "Teste",
                "teste@email.com",
                "123456",
                "11999999999",
                "Analista",
                "Estratégia",
                "junior",
                "USER",
                null,
                "Fazer X",
                "Bio",
                "linkedin",
                null
        );

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setId(1L);
        user.setName("Teste");
        user.setEmail("teste@email.com");
        user.setRole(role);

        when(userService.register(request)).thenReturn(user);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Teste"))
                .andExpect(jsonPath("$.email").value("teste@email.com"));
    }

    // ───────────────────────────────────────────────
    // TESTE: GET ALL USERS
    // ───────────────────────────────────────────────
    @Test
    void deveListarUsuarios() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Teste");
        user.setEmail("teste@email.com");

        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Teste"));
    }

    // ───────────────────────────────────────────────
    // TESTE: GET BY ID
    // ───────────────────────────────────────────────
    @Test
    void deveBuscarUsuarioPorId() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Teste");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teste"));
    }

    // ───────────────────────────────────────────────
    // TESTE: DELETE (soft delete)
    // ───────────────────────────────────────────────
    @Test
    void deveDeletarUsuario() throws Exception {

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}