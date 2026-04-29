package com.pina.mkt_api;

import com.pina.mkt_api.entities.*;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.repositories.RoleRepository;
import com.pina.mkt_api.services.BoardService;
import com.pina.mkt_api.services.CardService;
import com.pina.mkt_api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class KanbanFlowServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private BoardService boardService;
    @Autowired private CardService cardService;
    @Autowired private BoardColumnRepository columnRepository;
    @Autowired private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role role = new Role();
            role.setName("USER");
            role.setAccessKey("ROLE_USER");
            role.setDescription("Usuário Padrão");
            roleRepository.save(role);
        }
    }

    // ==========================================
    // BLOCO 1: USUÁRIOS
    // ==========================================

    @Test
    @DisplayName("Usuário: Deve registrar um novo usuário com sucesso")
    void shouldRegisterUserSuccessfully() {
        User user = createHelperUser("user1@tech.com");

        assertNotNull(user.getId(), "O ID do usuário não deveria ser nulo");
        assertTrue(user.getIsActive(), "O usuário deve nascer ativo");
        assertEquals("USER", user.getRole().getName(), "O usuário deve receber a role padrão");
    }

    @Test
    @DisplayName("Usuário: Deve bloquear e-mail duplicado (Regra de Negócio)")
    void shouldPreventDuplicateUserEmail() {
        createHelperUser("duplicado@tech.com");

        User user2 = new User();
        user2.setName("Clone");
        user2.setEmail("duplicado@tech.com");
        user2.setPassword("321");

        assertThrows(BusinessRuleException.class, () -> userService.register(user2),
                "A API deveria bloquear a criação por e-mail duplicado.");
    }

    // ==========================================
    // BLOCO 2: EMPRESAS
    // ==========================================

    @Test
    @DisplayName("Empresa: Deve criar uma empresa com sucesso")
    void shouldCreateCompanySuccessfully() {
        Company company = createHelperCompany("Tech Corp", "tech-corp");

        assertNotNull(company.getId(), "A empresa deve gerar um ID ao salvar");
        assertEquals("Tech Corp", company.getName());
    }

    // ==========================================
    // BLOCO 3: QUADROS (BOARDS)
    // ==========================================

    @Test
    @DisplayName("Quadro: Deve criar e amarrar à Empresa e aos Membros")
    void shouldCreateBoardLinkedToCompanyAndUsers() {
        User user = createHelperUser("membro@tech.com");
        Company company = createHelperCompany("Board Corp", "board-corp");

        Board board = new Board();
        board.setName("Quadro Principal");
        board.setBackgroundColor("#FFFFFF");
        board.setIsActive(true);

        Board savedBoard = boardService.create(company.getId(), board, List.of(user.getId()));

        assertNotNull(savedBoard.getId());
        assertEquals(company.getId(), savedBoard.getCompany().getId(), "Deve pertencer à empresa correta");
        assertEquals(1, savedBoard.getUsers().size(), "Deve conter 1 membro vinculado");
        assertEquals(user.getId(), savedBoard.getUsers().get(0).getId(), "O membro deve ser o usuário criado");
    }

    // ==========================================
    // BLOCO 4: COLUNAS
    // ==========================================

    @Test
    @DisplayName("Coluna: Deve criar e amarrar ao Quadro")
    void shouldCreateColumnLinkedToBoard() {
        User user = createHelperUser("coluna@tech.com");
        Company company = createHelperCompany("Col Corp", "col-corp");
        Board board = createHelperBoard(company, user);

        BoardColumn column = new BoardColumn();
        column.setName("Em Progresso");
        column.setPosition(1);
        column.setBoard(board);
        BoardColumn savedColumn = columnRepository.save(column);

        assertNotNull(savedColumn.getId());
        assertEquals(board.getId(), savedColumn.getBoard().getId(), "A coluna deve pertencer ao quadro correto");
        assertEquals("Em Progresso", savedColumn.getName());
    }

    // ==========================================
    // BLOCO 5: CARTÕES (CARDS)
    // ==========================================

    @Test
    @DisplayName("Cartão: Deve criar, amarrar à Coluna e atribuir Responsáveis")
    void shouldCreateCardLinkedToColumnAndAssignedUsers() {
        User dev = createHelperUser("dev@tech.com");
        Company company = createHelperCompany("Card Corp", "card-corp");
        Board board = createHelperBoard(company, dev);
        BoardColumn column = createHelperColumn(board);

        Card card = new Card();
        card.setTitle("Finalizar API");
        card.setDescription("Últimos testes do Kanban");
        card.setPosition(1);
        card.setIsActive(true);

        Card savedCard = cardService.createCard(column.getId(), card, List.of(dev.getId()));

        assertNotNull(savedCard.getId());
        assertEquals(column.getId(), savedCard.getColumn().getId(), "O cartão deve nascer na coluna certa");
        assertEquals(1, savedCard.getAssignedUsers().size(), "O cartão deve ter 1 responsável");
        assertEquals(dev.getId(), savedCard.getAssignedUsers().get(0).getId(), "O responsável deve ser o dev criado");
    }


    // ==========================================
    // MÉTODOS AUXILIARES (Fábrica de Dados)
    // ==========================================
    // Estes métodos evitam a repetição de código em cada teste acima.

    private User createHelperUser(String email) {
        User user = new User();
        user.setName("Usuário Teste");
        user.setEmail(email);
        user.setPassword("123");
        return userService.register(user);
    }

    private Company createHelperCompany(String name, String slug) {
        Company company = new Company();
        company.setName(name);
        company.setSlug(slug);
        company.setIsActive(true);
        return companyRepository.save(company);
    }

    private Board createHelperBoard(Company company, User user) {
        Board board = new Board();
        board.setName("Quadro Helper");
        board.setIsActive(true);
        return boardService.create(company.getId(), board, List.of(user.getId()));
    }

    private BoardColumn createHelperColumn(Board board) {
        BoardColumn column = new BoardColumn();
        column.setName("Coluna Helper");
        column.setPosition(1);
        column.setBoard(board);
        return columnRepository.save(column);
    }
}