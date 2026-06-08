package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.UserDTOs.UserRequestDTO;
import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.repositories.CardRepository;
import com.pina.mkt_api.repositories.RoleRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserService userService;

    private User buildUser(Long id, String email, boolean active) {
        User user = new User();
        user.setId(id);
        user.setName("Teste");
        user.setEmail(email);
        user.setPassword("encoded");
        user.setActive(active);
        return user;
    }

    @Nested
    @DisplayName("Testes do método register")
    class RegisterTests {

        @Test
        @DisplayName("Deve registrar usuário com sucesso")
        void deveRegistrarUsuarioComSucesso() {
            // arrange
            UserRequestDTO dto = new UserRequestDTO(
                    "Maria", "maria@email.com", "senha123",
                    null, null, null, "USER", null, null, null, null);

            Role role = new Role("USER", "ROLE_USER", "Usuário padrão");

            Mockito.when(userRepository.findByEmail("maria@email.com")).thenReturn(Optional.empty());
            Mockito.when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
            Mockito.when(passwordEncoder.encode("senha123")).thenReturn("encoded");
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            // act
            User resultado = userService.register(dto);

            // assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("maria@email.com", resultado.getEmail());
            Assertions.assertEquals("USER", resultado.getRole().getName());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException ao registrar e-mail duplicado")
        void deveLancarExcecaoEmailDuplicado() {
            // arrange
            UserRequestDTO dto = new UserRequestDTO(
                    "Maria", "maria@email.com", "senha123",
                    null, null, null, null, null, null, null, null);

            Mockito.when(userRepository.findByEmail("maria@email.com"))
                    .thenReturn(Optional.of(new User()));

            // act | assert
            BusinessRuleException exception = Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> userService.register(dto)
            );
            Assertions.assertEquals("Este e-mail já está em uso.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes do método findAllUsers")
    class FindAllUsersTests {

        @Test
        @DisplayName("Deve listar todos os usuários quando o perfil é admin")
        void deveListarTodosQuandoAdmin() {
            // arrange
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(userRepository.findByIsActiveTrue())
                    .thenReturn(List.of(buildUser(1L, "a@a.com", true)));

            // act
            List<User> resultado = userService.findAllUsers();

            // assert
            Assertions.assertEquals(1, resultado.size());
            Mockito.verify(userRepository).findByIsActiveTrue();
            Mockito.verify(userRepository, Mockito.never()).findMembersInSharedBoards(Mockito.any());
        }

        @Test
        @DisplayName("Deve listar apenas usuários de boards compartilhados quando não é admin")
        void deveListarUsuariosDeBoards() {
            // arrange
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("me@email.com");
            Mockito.when(userRepository.findMembersInSharedBoards("me@email.com"))
                    .thenReturn(List.of(buildUser(2L, "colega@email.com", true)));

            // act
            List<User> resultado = userService.findAllUsers();

            // assert
            Assertions.assertEquals(1, resultado.size());
            Mockito.verify(userRepository).findMembersInSharedBoards("me@email.com");
            Mockito.verify(userRepository, Mockito.never()).findByIsActiveTrue();
        }
    }

    @Nested
    @DisplayName("Testes do método login")
    class LoginTests {

        @Test
        @DisplayName("Deve fazer login com sucesso")
        void deveFazerLoginComSucesso() {
            // arrange
            User user = buildUser(1L, "user@email.com", true);
            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches("senha123", "encoded")).thenReturn(true);

            // act
            User resultado = userService.login("user@email.com", "senha123");

            // assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("user@email.com", resultado.getEmail());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando senha incorreta")
        void deveLancarExcecaoSenhaIncorreta() {
            // arrange
            User user = buildUser(1L, "user@email.com", true);
            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches("errada", "encoded")).thenReturn(false);

            // act | assert
            BusinessRuleException exception = Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> userService.login("user@email.com", "errada")
            );
            Assertions.assertEquals("Senha incorreta.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando usuário está inativo")
        void deveLancarExcecaoUsuarioInativo() {
            // arrange
            User user = buildUser(1L, "user@email.com", false);
            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

            // act | assert
            BusinessRuleException exception = Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> userService.login("user@email.com", "qualquer")
            );
            Assertions.assertEquals("Usuário desativado.", exception.getMessage());
        }
    }
}
