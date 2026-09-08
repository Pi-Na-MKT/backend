package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.security.SecurityUtils;
import com.pina.mkt_api.services.UserService;
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

@WebMvcTest(UserController.class)
@WithMockUser(roles = "ADMIN")
@DisplayName("Testes do UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private SecurityUtils securityUtils;

    private User buildUser(Long id, String name, String email) {
        Role role = new Role();
        role.setName("USER");
        role.setAccessKey("ROLE_USER");

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }

    @Nested
    @DisplayName("POST /api/users/register")
    class RegisterTests {

        @Test
        @DisplayName("Deve registrar usuário e retornar 201")
        void deveRegistrarUsuario() throws Exception {
            User usuario = buildUser(1L, "Teste", "teste@email.com");
            Mockito.when(userService.register(Mockito.any())).thenReturn(usuario);

            String corpo = """
                    {
                        "name": "Teste",
                        "email": "teste@email.com",
                        "password": "senha123"
                    }
                    """;

            mockMvc.perform(post("/api/users/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(201))
                    .andExpect(jsonPath("$.name").value("Teste"))
                    .andExpect(jsonPath("$.email").value("teste@email.com"));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar usuário por id corretamente")
        void deveRetornarUsuarioPorId() throws Exception {
            User usuario = buildUser(1L, "Teste", "teste@email.com");
            Mockito.when(userService.findById(1L)).thenReturn(usuario);

            mockMvc.perform(get("/api/users/{id}", 1))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Teste"))
                    .andExpect(jsonPath("$.email").value("teste@email.com"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando usuário não encontrado")
        void deveRetornarNotFoundQuandoNaoEncontrado() throws Exception {
            Mockito.when(userService.findById(99L))
                    .thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

            mockMvc.perform(get("/api/users/{id}", 99))
                    .andExpect(status().is(404));
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class ListarTests {

        @Test
        @DisplayName("Deve listar usuários corretamente")
        void deveListarUsuarios() throws Exception {
            Mockito.when(userService.findAllUsers())
                    .thenReturn(List.of(
                            buildUser(1L, "Teste", "teste@email.com"),
                            buildUser(2L, "Outro", "outro@email.com")
                    ));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$[0].name").value("Teste"))
                    .andExpect(jsonPath("$[1].name").value("Outro"));
        }
    }

    @Nested
    @DisplayName("POST /api/users/login")
    class LoginTests {

        @Test
        @DisplayName("Deve realizar login e retornar token JWT")
        void deveRealizarLogin() throws Exception {
            User usuario = buildUser(1L, "Teste", "teste@email.com");
            Mockito.when(userService.login("teste@email.com", "senha123")).thenReturn(usuario);
            Mockito.when(jwtUtil.generateToken(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn("mock-jwt-token");

            String corpo = """
                    {
                        "email": "teste@email.com",
                        "password": "senha123"
                    }
                    """;

            mockMvc.perform(post("/api/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                    .andExpect(jsonPath("$.name").value("Teste"));
        }
    }
}
