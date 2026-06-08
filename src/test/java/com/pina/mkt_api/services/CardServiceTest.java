package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.*;
import com.pina.mkt_api.enums.Priority;
import com.pina.mkt_api.events.CardCompletedEvent;
import com.pina.mkt_api.events.CardCreatedEvent;
import com.pina.mkt_api.events.CardMovedEvent;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.*;
import com.pina.mkt_api.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe CardService")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private BoardColumnRepository columnRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CardService cardService;

    private Card buildCard(Long id, String title, boolean completed) {
        Company company = new Company();
        company.setId(1L);

        Board board = new Board();
        board.setId(1L);
        board.setCompany(company);

        BoardColumn column = new BoardColumn();
        column.setId(1L);
        column.setBoard(board);

        Card card = new Card();
        card.setId(id);
        card.setTitle(title);
        card.setPriority(Priority.HIGH);
        card.setPosition(1);
        card.setIsActive(true);
        card.setCompleted(completed);
        card.setColumn(column);
        return card;
    }

    @Nested
    @DisplayName("Testes do método createCard")
    class CreateCardTests {

        @Test
        @DisplayName("Deve criar card e publicar CardCreatedEvent")
        void deveCriarCardEPublicarEvento() {
            Board board = new Board();
            board.setId(1L);

            BoardColumn column = new BoardColumn();
            column.setId(1L);
            column.setBoard(board);

            Card card = new Card();
            card.setTitle("Nova Tarefa");
            card.setPriority(Priority.HIGH);
            card.setPosition(1);

            Mockito.when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
            Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(inv -> {
                Card c = inv.getArgument(0);
                c.setId(10L);
                return c;
            });

            Card resultado = cardService.createCard(1L, card, null);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("Nova Tarefa", resultado.getTitle());
            Mockito.verify(eventPublisher).publishEvent(Mockito.any(CardCreatedEvent.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando coluna não encontrada")
        void deveLancarExcecaoColunaNotFound() {
            Mockito.when(columnRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.createCard(99L, new Card(), null)
            );
            Mockito.verify(eventPublisher, Mockito.never()).publishEvent(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Testes do método findAllCards")
    class FindAllCardsTests {

        @Test
        @DisplayName("Admin deve retornar todos os cards")
        void adminDeveRetornarTodos() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(cardRepository.findAll()).thenReturn(List.of(
                    buildCard(1L, "Card 1", false),
                    buildCard(2L, "Card 2", false)
            ));

            List<Card> resultado = cardService.findAllCards();

            Assertions.assertEquals(2, resultado.size());
            Mockito.verify(cardRepository).findAll();
            Mockito.verify(cardRepository, Mockito.never()).findAccessibleByUserEmail(Mockito.any());
        }

        @Test
        @DisplayName("Não-admin deve retornar apenas cards acessíveis ao usuário")
        void naoAdminDeveRetornarApenasAcessiveis() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("user@email.com");
            Mockito.when(cardRepository.findAccessibleByUserEmail("user@email.com"))
                    .thenReturn(List.of(buildCard(1L, "Minha Tarefa", false)));

            List<Card> resultado = cardService.findAllCards();

            Assertions.assertEquals(1, resultado.size());
            Mockito.verify(cardRepository).findAccessibleByUserEmail("user@email.com");
            Mockito.verify(cardRepository, Mockito.never()).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método updateCard")
    class UpdateCardTests {

        @Test
        @DisplayName("Deve atualizar título sem alterar campos não informados")
        void deveAtualizarTituloSemAlterarOutros() {
            Card existente = buildCard(1L, "Título Antigo", false);
            existente.setDescription("Descrição existente");

            Card dados = new Card();
            dados.setTitle("Título Novo");

            Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(existente));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

            Card resultado = cardService.updateCard(1L, dados, null);

            Assertions.assertEquals("Título Novo", resultado.getTitle());
            Assertions.assertEquals("Descrição existente", resultado.getDescription());
            Mockito.verify(eventPublisher, Mockito.never()).publishEvent(Mockito.any());
        }

        @Test
        @DisplayName("Deve publicar CardCompletedEvent quando card muda de pendente para concluído")
        void devePublicarEventoAoConcluirCard() {
            Card existente = buildCard(1L, "Tarefa", false);

            Card dados = new Card();
            dados.setCompleted(true);

            Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(existente));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

            cardService.updateCard(1L, dados, null);

            Mockito.verify(eventPublisher).publishEvent(Mockito.any(CardCompletedEvent.class));
        }

        @Test
        @DisplayName("Não deve publicar CardCompletedEvent quando card já estava concluído")
        void naoDevePublicarEventoSeJaConcluido() {
            Card existente = buildCard(1L, "Tarefa", true);

            Card dados = new Card();
            dados.setCompleted(true);

            Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(existente));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

            cardService.updateCard(1L, dados, null);

            Mockito.verify(eventPublisher, Mockito.never()).publishEvent(Mockito.any(CardCompletedEvent.class));
        }
    }

    @Nested
    @DisplayName("Testes do método moveCard")
    class MoveCardTests {

        @Test
        @DisplayName("Deve mover card para outra coluna e publicar CardMovedEvent")
        void deveMoverCardEPublicarEvento() {
            Card card = buildCard(1L, "Tarefa", false); // column.board.id = 1

            Board board = new Board();
            board.setId(1L); // mesmo board
            BoardColumn destino = new BoardColumn();
            destino.setId(2L);
            destino.setBoard(board);

            Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(columnRepository.findById(2L)).thenReturn(Optional.of(destino));
            Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

            Card resultado = cardService.moveCard(1L, 2L, 0);

            Assertions.assertEquals(destino, resultado.getColumn());
            Assertions.assertEquals(0, resultado.getPosition());
            Mockito.verify(eventPublisher).publishEvent(Mockito.any(CardMovedEvent.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException ao mover para coluna de outro board")
        void deveLancarExcecaoParaColunaDeOutroBoard() {
            Card card = buildCard(1L, "Tarefa", false); // column.board.id = 1

            Board outraBoard = new Board();
            outraBoard.setId(99L);
            BoardColumn colunaDiferente = new BoardColumn();
            colunaDiferente.setId(5L);
            colunaDiferente.setBoard(outraBoard);

            Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(columnRepository.findById(5L)).thenReturn(Optional.of(colunaDiferente));

            Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> cardService.moveCard(1L, 5L, 0)
            );
            Mockito.verify(eventPublisher, Mockito.never()).publishEvent(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Testes do método deleteCard")
    class DeleteCardTests {

        @Test
        @DisplayName("Deve deletar card com sucesso")
        void deveDeletarCardComSucesso() {
            Card card = buildCard(1L, "Tarefa", false);
            Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);

            cardService.deleteCard(1L);

            Mockito.verify(cardRepository).delete(card);
        }
    }
}
