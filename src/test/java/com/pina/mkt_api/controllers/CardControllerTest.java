package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.enums.Priority;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.services.CardService;
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

@WebMvcTest(CardController.class)
@WithMockUser(roles = "ADMIN")
@DisplayName("Testes do CardController")
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;
    @MockBean
    private JwtUtil jwtUtil;

    private Card buildCard(Long id, String title) {
        BoardColumn column = new BoardColumn();
        column.setId(1L);

        Card card = new Card();
        card.setId(id);
        card.setTitle(title);
        card.setPriority(Priority.HIGH);
        card.setPosition(1);
        card.setIsActive(true);
        card.setCompleted(false);
        card.setColumn(column);
        return card;
    }

    @Nested
    @DisplayName("POST /api/cards/column/{columnId}")
    class CriarCardTests {

        @Test
        @DisplayName("Deve criar card e retornar 201")
        void deveCriarCard() throws Exception {
            Card card = buildCard(1L, "Nova Tarefa");
            Mockito.when(cardService.createCard(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                    .thenReturn(card);

            String corpo = """
                    {
                        "title": "Nova Tarefa",
                        "priority": "HIGH",
                        "position": 1
                    }
                    """;

            mockMvc.perform(post("/api/cards/column/{columnId}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(201))
                    .andExpect(jsonPath("$.title").value("Nova Tarefa"))
                    .andExpect(jsonPath("$.priority").value("HIGH"))
                    .andExpect(jsonPath("$.columnId").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/cards")
    class ListarCardsTests {

        @Test
        @DisplayName("Deve listar cards acessíveis ao usuário")
        void deveListarCards() throws Exception {
            Mockito.when(cardService.findAllCards()).thenReturn(List.of(
                    buildCard(1L, "Tarefa 1"),
                    buildCard(2L, "Tarefa 2")
            ));

            mockMvc.perform(get("/api/cards"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$[0].title").value("Tarefa 1"))
                    .andExpect(jsonPath("$[1].title").value("Tarefa 2"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há cards acessíveis")
        void deveRetornarListaVazia() throws Exception {
            Mockito.when(cardService.findAllCards()).thenReturn(List.of());

            mockMvc.perform(get("/api/cards"))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/cards/{id}")
    class BuscarCardPorIdTests {

        @Test
        @DisplayName("Deve retornar card por id")
        void deveRetornarCardPorId() throws Exception {
            Card card = buildCard(1L, "Tarefa 1");
            Mockito.when(cardService.findById(1L)).thenReturn(card);

            mockMvc.perform(get("/api/cards/{id}", 1))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Tarefa 1"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando card não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            Mockito.when(cardService.findById(99L))
                    .thenThrow(new ResourceNotFoundException("Card não encontrado"));

            mockMvc.perform(get("/api/cards/{id}", 99))
                    .andExpect(status().is(404));
        }
    }

    @Nested
    @DisplayName("PUT /api/cards/{id}")
    class AtualizarCardTests {

        @Test
        @DisplayName("Deve atualizar card e retornar 200")
        void deveAtualizarCard() throws Exception {
            Card card = buildCard(1L, "Tarefa Atualizada");
            Mockito.when(cardService.updateCard(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                    .thenReturn(card);

            String corpo = """
                    {
                        "title": "Tarefa Atualizada"
                    }
                    """;

            mockMvc.perform(put("/api/cards/{id}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("Tarefa Atualizada"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/cards/{id}/move")
    class MoverCardTests {

        @Test
        @DisplayName("Deve mover card e retornar 200")
        void deveMoverCard() throws Exception {
            Card card = buildCard(1L, "Tarefa");
            card.getColumn().setId(2L);
            Mockito.when(cardService.moveCard(Mockito.eq(1L), Mockito.eq(2L), Mockito.eq(0)))
                    .thenReturn(card);

            String corpo = """
                    {
                        "columnId": 2,
                        "position": 0
                    }
                    """;

            mockMvc.perform(patch("/api/cards/{id}/move", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.columnId").value(2));
        }
    }

    @Nested
    @DisplayName("DELETE /api/cards/{id}")
    class DeletarCardTests {

        @Test
        @DisplayName("Deve deletar card e retornar 204")
        void deveDeletarCard() throws Exception {
            Mockito.doNothing().when(cardService).deleteCard(1L);

            mockMvc.perform(delete("/api/cards/{id}", 1).with(csrf()))
                    .andExpect(status().is(204));
        }
    }
}
