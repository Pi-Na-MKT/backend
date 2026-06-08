package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe BoardService")
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private BoardService boardService;

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
    @DisplayName("Testes do método create")
    class CreateTests {

        @Test
        @DisplayName("Deve criar board com sucesso vinculando a empresa")
        void deveCriarBoardComSucesso() {
            Company empresa = new Company();
            empresa.setId(1L);
            empresa.setName("Tech Co");

            Board board = new Board();
            board.setName("Projeto X");

            Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(empresa));
            Mockito.when(boardRepository.save(Mockito.any(Board.class))).thenAnswer(inv -> {
                Board b = inv.getArgument(0);
                b.setId(1L);
                return b;
            });

            Board resultado = boardService.create(1L, board, null);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("Projeto X", resultado.getName());
            Assertions.assertEquals(empresa, resultado.getCompany());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando empresa não encontrada")
        void deveLancarExcecaoEmpresaNaoEncontrada() {
            Mockito.when(companyRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> boardService.create(99L, new Board(), null)
            );
        }
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar todos os boards quando admin")
        void deveRetornarTodosQuandoAdmin() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(boardRepository.findAll()).thenReturn(List.of(
                    buildBoard(1L, "Board 1"),
                    buildBoard(2L, "Board 2")
            ));

            List<Board> resultado = boardService.findAll();

            Assertions.assertEquals(2, resultado.size());
            Mockito.verify(boardRepository).findAll();
            Mockito.verify(boardRepository, Mockito.never()).findByUsersEmail(Mockito.any());
        }

        @Test
        @DisplayName("Deve retornar apenas boards do usuário quando não é admin")
        void deveRetornarBoardsDoUsuario() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("user@email.com");
            Mockito.when(boardRepository.findByUsersEmail("user@email.com"))
                    .thenReturn(List.of(buildBoard(1L, "Meu Board")));

            List<Board> resultado = boardService.findAll();

            Assertions.assertEquals(1, resultado.size());
            Mockito.verify(boardRepository).findByUsersEmail("user@email.com");
            Mockito.verify(boardRepository, Mockito.never()).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar board quando admin")
        void deveRetornarBoardQuandoAdmin() {
            Board board = buildBoard(1L, "Board 1");
            Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);

            Board resultado = boardService.findById(1L);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("Board 1", resultado.getName());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando board não existe")
        void deveLancarExcecaoBoardNaoEncontrado() {
            Mockito.when(boardRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(
                    ResourceNotFoundException.class,
                    () -> boardService.findById(99L)
            );
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar nome do board sem alterar descrição existente")
        void deveAtualizarNomeSemAlterarDescricao() {
            Board existente = buildBoard(1L, "Nome Antigo");
            existente.setDescription("Descrição existente");

            Board dados = new Board();
            dados.setName("Nome Novo");

            Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(existente));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(boardRepository.save(Mockito.any(Board.class))).thenAnswer(inv -> inv.getArgument(0));

            Board resultado = boardService.update(1L, dados, null);

            Assertions.assertEquals("Nome Novo", resultado.getName());
            Assertions.assertEquals("Descrição existente", resultado.getDescription());
        }
    }

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar board com sucesso")
        void deveDeletarBoardComSucesso() {
            Board board = buildBoard(1L, "Board 1");
            Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);

            boardService.delete(1L);

            Mockito.verify(boardRepository).delete(board);
        }
    }
}
