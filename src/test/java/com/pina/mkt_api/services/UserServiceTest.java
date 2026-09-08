package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.UserDTOs.PasswordChangeDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserRequestDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserUpdateDTO;
import com.pina.mkt_api.entities.Role;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
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
            UserRequestDTO dto = new UserRequestDTO(
                    "Maria", "maria@email.com", "senha123",
                    null, null, null, "USER", null, null, null, null);

            Role role = new Role("USER", "ROLE_USER", "Usuário padrão");

            Mockito.when(userRepository.findByEmail("maria@email.com")).thenReturn(Optional.empty());
            Mockito.when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
            Mockito.when(passwordEncoder.encode("senha123")).thenReturn("encoded");
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User resultado = userService.register(dto);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("maria@email.com", resultado.getEmail());
            Assertions.assertEquals("USER", resultado.getRole().getName());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException ao registrar e-mail duplicado")
        void deveLancarExcecaoEmailDuplicado() {
            UserRequestDTO dto = new UserRequestDTO(
                    "Maria", "maria@email.com", "senha123",
                    null, null, null, null, null, null, null, null);

            Mockito.when(userRepository.findByEmail("maria@email.com"))
                    .thenReturn(Optional.of(new User()));

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
            Mockito.when(securityUtils.isAdmin()).thenReturn(true);
            Mockito.when(userRepository.findByIsActiveTrue())
                    .thenReturn(List.of(buildUser(1L, "a@a.com", true)));

            List<User> resultado = userService.findAllUsers();

            Assertions.assertEquals(1, resultado.size());
            Mockito.verify(userRepository).findByIsActiveTrue();
            Mockito.verify(userRepository, Mockito.never()).findMembersInSharedBoards(Mockito.any());
        }

        @Test
        @DisplayName("Deve listar apenas usuários de boards compartilhados quando não é admin")
        void deveListarUsuariosDeBoards() {
            Mockito.when(securityUtils.isAdmin()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("me@email.com");
            Mockito.when(userRepository.findMembersInSharedBoards("me@email.com"))
                    .thenReturn(List.of(buildUser(2L, "colega@email.com", true)));

            List<User> resultado = userService.findAllUsers();

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
            User user = buildUser(1L, "user@email.com", true);
            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches("senha123", "encoded")).thenReturn(true);

            User resultado = userService.login("user@email.com", "senha123");

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("user@email.com", resultado.getEmail());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando senha incorreta")
        void deveLancarExcecaoSenhaIncorreta() {
            User user = buildUser(1L, "user@email.com", true);
            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches("errada", "encoded")).thenReturn(false);

            BusinessRuleException exception = Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> userService.login("user@email.com", "errada")
            );
            Assertions.assertEquals("Senha incorreta.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando usuário está inativo")
        void deveLancarExcecaoUsuarioInativo() {
            User user = buildUser(1L, "user@email.com", false);
            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

            BusinessRuleException exception = Assertions.assertThrows(
                    BusinessRuleException.class,
                    () -> userService.login("user@email.com", "qualquer")
            );
            Assertions.assertEquals("Usuário desativado.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Admin/manager deve obter qualquer usuário ativo")
        void adminDeveObterUsuario() {
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "a@a.com", true)));
            Mockito.when(securityUtils.isAdminOrManager()).thenReturn(true);

            User resultado = userService.findById(1L);

            Assertions.assertEquals("a@a.com", resultado.getEmail());
        }

        @Test
        @DisplayName("Usuário comum deve obter o próprio perfil")
        void deveObterProprioPerfil() {
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "me@email.com", true)));
            Mockito.when(securityUtils.isAdminOrManager()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("me@email.com");
            Mockito.when(userRepository.findByEmail("me@email.com"))
                    .thenReturn(Optional.of(buildUser(1L, "me@email.com", true)));

            User resultado = userService.findById(1L);

            Assertions.assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void deveLancarExcecaoQuandoNaoExiste() {
            Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
        }

        @Test
        @DisplayName("Deve negar acesso ao perfil de outro usuário quando não é admin/manager")
        void deveNegarAcessoAOutroPerfil() {
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "alvo@email.com", true)));
            Mockito.when(securityUtils.isAdminOrManager()).thenReturn(false);
            Mockito.when(securityUtils.getAuthenticatedEmail()).thenReturn("outro@email.com");
            Mockito.when(userRepository.findByEmail("outro@email.com"))
                    .thenReturn(Optional.of(buildUser(2L, "outro@email.com", true)));

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Testes do método findMe")
    class FindMeTests {

        @Test
        @DisplayName("Deve retornar o usuário autenticado pelo e-mail")
        void deveRetornarUsuarioAutenticado() {
            Mockito.when(userRepository.findByEmail("me@email.com"))
                    .thenReturn(Optional.of(buildUser(1L, "me@email.com", true)));

            User resultado = userService.findMe("me@email.com");

            Assertions.assertEquals("me@email.com", resultado.getEmail());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o e-mail não existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            Mockito.when(userRepository.findByEmail("x@email.com")).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.findMe("x@email.com"));
        }
    }

    @Nested
    @DisplayName("Testes do método createUser")
    class CreateUserTests {

        @Test
        @DisplayName("Deve criar usuário administrativamente com sucesso")
        void deveCriarComSucesso() {
            User novo = new User();
            novo.setEmail("novo@email.com");
            novo.setPassword("raw");
            Role role = new Role("USER", "ROLE_USER", "Usuário padrão");

            Mockito.when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
            Mockito.when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
            Mockito.when(passwordEncoder.encode("raw")).thenReturn("encoded");
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User resultado = userService.createUser(novo, 2L);

            Assertions.assertTrue(resultado.getActive());
            Assertions.assertEquals("encoded", resultado.getPassword());
            Assertions.assertEquals(role, resultado.getRole());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando o e-mail já está em uso")
        void deveLancarExcecaoEmailEmUso() {
            User novo = new User();
            novo.setEmail("dup@email.com");
            Mockito.when(userRepository.findByEmail("dup@email.com")).thenReturn(Optional.of(new User()));

            Assertions.assertThrows(BusinessRuleException.class, () -> userService.createUser(novo, 1L));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando o roleId é nulo")
        void deveLancarExcecaoRoleIdNulo() {
            User novo = new User();
            novo.setEmail("novo@email.com");
            Mockito.when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());

            Assertions.assertThrows(BusinessRuleException.class, () -> userService.createUser(novo, null));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o perfil não existe")
        void deveLancarExcecaoRoleNaoEncontrada() {
            User novo = new User();
            novo.setEmail("novo@email.com");
            Mockito.when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
            Mockito.when(roleRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.createUser(novo, 99L));
        }
    }

    @Nested
    @DisplayName("Testes do método updateUser")
    class UpdateUserTests {

        @Test
        @DisplayName("Deve atualizar apenas os campos informados")
        void deveAtualizarCamposInformados() {
            User existente = buildUser(1L, "user@email.com", true);
            existente.setName("Nome Antigo");
            UserUpdateDTO dto = new UserUpdateDTO("Nome Novo", "11 90000-0000", "Dev",
                    null, null, null, null, null);

            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existente));
            Mockito.when(securityUtils.isAdminOrManager()).thenReturn(true);
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User resultado = userService.updateUser(1L, dto);

            Assertions.assertEquals("Nome Novo", resultado.getName());
            Assertions.assertEquals("Dev", resultado.getJobTitle());
        }
    }

    @Nested
    @DisplayName("Testes do método changePassword")
    class ChangePasswordTests {

        @Test
        @DisplayName("Deve alterar a senha com sucesso")
        void deveAlterarSenhaComSucesso() {
            User user = buildUser(1L, "user@email.com", true);
            PasswordChangeDTO dto = new PasswordChangeDTO("atual", "novaSenha123");

            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches("atual", "encoded")).thenReturn(true);
            Mockito.when(passwordEncoder.encode("novaSenha123")).thenReturn("novaEncoded");

            userService.changePassword("user@email.com", dto);

            Assertions.assertEquals("novaEncoded", user.getPassword());
            Mockito.verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando a senha atual está incorreta")
        void deveLancarExcecaoSenhaAtualIncorreta() {
            User user = buildUser(1L, "user@email.com", true);
            PasswordChangeDTO dto = new PasswordChangeDTO("errada", "novaSenha123");

            Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches("errada", "encoded")).thenReturn(false);

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> userService.changePassword("user@email.com", dto));
        }
    }

    @Nested
    @DisplayName("Testes do método changeRole")
    class ChangeRoleTests {

        @Test
        @DisplayName("Deve alterar o perfil do usuário com sucesso")
        void deveAlterarRoleComSucesso() {
            User user = buildUser(1L, "user@email.com", true);
            Role role = new Role("MANAGER", "ROLE_MANAGER", "Gestor");

            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User resultado = userService.changeRole(1L, 2L);

            Assertions.assertEquals(role, resultado.getRole());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void deveLancarExcecaoUsuarioNaoEncontrado() {
            Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.changeRole(99L, 1L));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o perfil não existe")
        void deveLancarExcecaoPerfilNaoEncontrado() {
            User user = buildUser(1L, "user@email.com", true);
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(roleRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.changeRole(1L, 99L));
        }
    }

    @Nested
    @DisplayName("Testes do método deleteUser")
    class DeleteUserTests {

        @Test
        @DisplayName("Deve desativar o usuário e removê-lo de boards e cards")
        void deveDesativarUsuario() {
            User user = buildUser(1L, "user@email.com", true);
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.deleteUser(1L);

            Assertions.assertFalse(user.getActive());
            Mockito.verify(boardRepository).removeUserFromAllBoards(1L);
            Mockito.verify(cardRepository).removeUserFromAllCards(1L);
            Mockito.verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void deveLancarExcecaoAoDeletarInexistente() {
            Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
        }
    }

    @Nested
    @DisplayName("Testes do método isOwnProfile")
    class IsOwnProfileTests {

        @Test
        @DisplayName("Deve retornar true quando o e-mail pertence ao próprio id")
        void deveRetornarTrue() {
            Mockito.when(userRepository.findByEmail("me@email.com"))
                    .thenReturn(Optional.of(buildUser(1L, "me@email.com", true)));

            Assertions.assertTrue(userService.isOwnProfile(1L, "me@email.com"));
        }

        @Test
        @DisplayName("Deve retornar false quando o e-mail não existe")
        void deveRetornarFalse() {
            Mockito.when(userRepository.findByEmail("x@email.com")).thenReturn(Optional.empty());

            Assertions.assertFalse(userService.isOwnProfile(1L, "x@email.com"));
        }
    }
}
