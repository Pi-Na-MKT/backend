package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.BoardColumnDTOs.BoardColumnUpdateDTO;
import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe BoardColumnService")
class BoardColumnServiceTest {

    @Mock
    private BoardColumnRepository columnRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private BoardColumnService boardColumnService;

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
    @DisplayName("Testes do método create")
    class CreateTests {

        @Test
        @DisplayName("Deve criar coluna vinculando ao board")
        void deveCriarColunaComSucesso() {
            Board board = new Board();
            board.setId(1L);
            board.setName("Projeto X");

            BoardColumn column = new BoardColumn();
            column.setName("A Fazer");
            column.setPosition(1);

            Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
            Mockito.when(columnRepository.save(Mockito.any(BoardColumn.class))).thenAnswer(inv -> {
                BoardColumn c = inv.getArgument(0);
                c.setId(1L);
                return c;
            });

            BoardColumn resultado = boardColumnService.create(1L, column);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("A Fazer", resultado.getName());
            Assertions.assertEquals(board, resultado.getBoard());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando board não encontrado")
        void deveLancarExcecaoBoardNaoEncontrado() {
            Mockito.when(boardRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> boardColumnService.create(99L, new BoardColumn())
            );
        }
    }

    @Nested
    @DisplayName("Testes do método getByBoard")
    class GetByBoardTests {

        @Test
        @DisplayName("Admin deve listar colunas sem verificar acesso")
        void adminDeveListarColunasSemVerificarAcesso() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(columnRepository.findByBoardId(1L)).thenReturn(List.of(
                    buildColumn(1L, "A Fazer"),
                    buildColumn(2L, "Em Progresso")
            ));

            List<BoardColumn> resultado = boardColumnService.getByBoard(1L);

            Assertions.assertEquals(2, resultado.size());
            Mockito.verify(boardRepository, Mockito.never()).existsByIdAndUsersEmail(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando usuário não é membro do board")
        void deveLancarExcecaoUsuarioNaoEhMembro() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("user@email.com");
            Mockito.when(boardRepository.existsByIdAndUsersEmail(1L, "user@email.com")).thenReturn(false);

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> boardColumnService.getByBoard(1L)
            );
        }
    }

    @Nested
    @DisplayName("Testes do método updateColumn")
    class UpdateColumnTests {

        @Test
        @DisplayName("Deve atualizar nome sem alterar posição existente")
        void deveAtualizarNomeSemAlterarPosicao() {
            BoardColumn coluna = buildColumn(1L, "Nome Antigo");
            coluna.setPosition(2);

            BoardColumnUpdateDTO dto = new BoardColumnUpdateDTO("Nome Novo", null);

            Mockito.when(columnRepository.findById(1L)).thenReturn(Optional.of(coluna));
            Mockito.when(columnRepository.save(Mockito.any(BoardColumn.class))).thenAnswer(inv -> inv.getArgument(0));

            BoardColumn resultado = boardColumnService.updateColumn(1L, dto);

            Assertions.assertEquals("Nome Novo", resultado.getName());
            Assertions.assertEquals(2, resultado.getPosition());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando coluna não encontrada")
        void deveLancarExcecaoColunaNotFound() {
            Mockito.when(columnRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> boardColumnService.updateColumn(99L, new BoardColumnUpdateDTO(null, null))
            );
        }
    }

    @Nested
    @DisplayName("Testes do método deleteColumn")
    class DeleteColumnTests {

        @Test
        @DisplayName("Deve deletar coluna com sucesso")
        void deveDeletarColunaComSucesso() {
            BoardColumn coluna = buildColumn(1L, "A Fazer");
            Mockito.when(columnRepository.findById(1L)).thenReturn(Optional.of(coluna));

            boardColumnService.deleteColumn(1L);

            Mockito.verify(columnRepository).delete(coluna);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao deletar coluna inexistente")
        void deveLancarExcecaoAoDeletarNaoExistente() {
            Mockito.when(columnRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> boardColumnService.deleteColumn(99L)
            );
        }
    }
}
