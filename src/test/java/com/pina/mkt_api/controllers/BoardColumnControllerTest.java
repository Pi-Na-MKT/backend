package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.services.BoardColumnService;
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

@WebMvcTest(BoardColumnController.class)
@WithMockUser(roles = "ADMIN")
@DisplayName("Testes do BoardColumnController")
class BoardColumnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardColumnService boardColumnService;
    @MockBean
    private JwtUtil jwtUtil;

    private BoardColumn buildColumn(Long id, String name) {
        Board board = new Board();
        board.setId(1L);

        BoardColumn column = new BoardColumn();
        column.setId(id);
        column.setName(name);
        column.setPosition(1);
        column.setBoard(board);
        return column;
    }

    @Nested
    @DisplayName("POST /api/columns/board/{boardId}")
    class CriarTests {

        @Test
        @DisplayName("Deve criar coluna e retornar 201")
        void deveCriarColuna() throws Exception {
            BoardColumn coluna = buildColumn(1L, "A Fazer");
            Mockito.when(boardColumnService.create(Mockito.eq(1L), Mockito.any())).thenReturn(coluna);

            String corpo = """
                    {
                        "name": "A Fazer",
                        "position": 1
                    }
                    """;

            mockMvc.perform(post("/api/columns/board/{boardId}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(201))
                    .andExpect(jsonPath("$.name").value("A Fazer"))
                    .andExpect(jsonPath("$.position").value(1))
                    .andExpect(jsonPath("$.boardId").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/columns/board/{boardId}")
    class ListarTests {

        @Test
        @DisplayName("Deve listar colunas do board")
        void deveListarColunas() throws Exception {
            Mockito.when(boardColumnService.getByBoard(1L)).thenReturn(List.of(
                    buildColumn(1L, "A Fazer"),
                    buildColumn(2L, "Em Progresso"),
                    buildColumn(3L, "Concluído")
            ));

            mockMvc.perform(get("/api/columns/board/{boardId}", 1))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$[0].name").value("A Fazer"))
                    .andExpect(jsonPath("$[1].name").value("Em Progresso"))
                    .andExpect(jsonPath("$[2].name").value("Concluído"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando board não encontrado ou sem acesso")
        void deveRetornar404QuandoBoardNaoEncontrado() throws Exception {
            Mockito.when(boardColumnService.getByBoard(99L))
                    .thenThrow(new ResourceNotFoundException("Board não encontrado"));

            mockMvc.perform(get("/api/columns/board/{boardId}", 99))
                    .andExpect(status().is(404));
        }
    }

    @Nested
    @DisplayName("PUT /api/columns/{id}")
    class AtualizarTests {

        @Test
        @DisplayName("Deve atualizar coluna e retornar 200")
        void deveAtualizarColuna() throws Exception {
            BoardColumn coluna = buildColumn(1L, "Em Progresso");
            coluna.setPosition(2);
            Mockito.when(boardColumnService.updateColumn(Mockito.eq(1L), Mockito.any())).thenReturn(coluna);

            String corpo = """
                    {
                        "name": "Em Progresso",
                        "position": 2
                    }
                    """;

            mockMvc.perform(put("/api/columns/{id}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.name").value("Em Progresso"))
                    .andExpect(jsonPath("$.position").value(2));
        }
    }

    @Nested
    @DisplayName("DELETE /api/columns/{id}")
    class DeletarTests {

        @Test
        @DisplayName("Deve deletar coluna e retornar 204")
        void deveDeletarColuna() throws Exception {
            Mockito.doNothing().when(boardColumnService).deleteColumn(1L);

            mockMvc.perform(delete("/api/columns/{id}", 1).with(csrf()))
                    .andExpect(status().is(204));
        }
    }
}
