package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.services.BoardService;
import org.junit.jupiter.api.*;
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

@WebMvcTest(BoardController.class)
@WithMockUser(roles = "ADMIN")
@DisplayName("Testes do BoardController")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;
    @MockBean
    private JwtUtil jwtUtil;

    private Board buildBoard(Long id, String name) {
        Company company = new Company();
        company.setId(1L);

        Board board = new Board();
        board.setId(id);
        board.setName(name);
        board.setIsActive(true);
        board.setCompany(company);
        board.setUsers(List.of());
        return board;
    }

    @Nested
    @DisplayName("POST /api/boards/company/{companyId}")
    class CriarTests {

        @Test
        @DisplayName("Deve criar board e retornar 201")
        void deveCriarBoard() throws Exception {
            Board board = buildBoard(1L, "Projeto Marketing");
            Mockito.when(boardService.create(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                    .thenReturn(board);

            String corpo = """
                    {
                        "name": "Projeto Marketing",
                        "isActive": true
                    }
                    """;

            mockMvc.perform(post("/api/boards/company/{companyId}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(201))
                    .andExpect(jsonPath("$.name").value("Projeto Marketing"))
                    .andExpect(jsonPath("$.companyId").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/boards")
    class ListarTests {

        @Test
        @DisplayName("Deve listar boards corretamente")
        void deveListarBoards() throws Exception {
            Mockito.when(boardService.findAll()).thenReturn(List.of(
                    buildBoard(1L, "Board 1"),
                    buildBoard(2L, "Board 2")
            ));

            mockMvc.perform(get("/api/boards"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$[0].name").value("Board 1"))
                    .andExpect(jsonPath("$[1].name").value("Board 2"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há boards")
        void deveRetornarListaVazia() throws Exception {
            Mockito.when(boardService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/boards"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/boards/{id}")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar board por id")
        void deveRetornarBoardPorId() throws Exception {
            Board board = buildBoard(1L, "Board 1");
            Mockito.when(boardService.findById(1L)).thenReturn(board);

            mockMvc.perform(get("/api/boards/{id}", 1))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Board 1"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando board não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            Mockito.when(boardService.findById(99L))
                    .thenThrow(new ResourceNotFoundException("Board não encontrado"));

            mockMvc.perform(get("/api/boards/{id}", 99))
                    .andExpect(status().is(404));
        }
    }

    @Nested
    @DisplayName("GET /api/boards/{id}/members")
    class MembrosTests {

        @Test
        @DisplayName("Deve retornar lista de membros do board")
        void deveRetornarMembrosDoBoard() throws Exception {
            Board board = buildBoard(1L, "Board 1");
            Mockito.when(boardService.findById(1L)).thenReturn(board);

            mockMvc.perform(get("/api/boards/{id}/members", 1))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("DELETE /api/boards/{id}")
    class DeletarTests {

        @Test
        @DisplayName("Deve deletar board e retornar 204")
        void deveDeletarBoard() throws Exception {
            Mockito.doNothing().when(boardService).delete(1L);

            mockMvc.perform(delete("/api/boards/{id}", 1).with(csrf()))
                    .andExpect(status().is(204));
        }
    }
}
