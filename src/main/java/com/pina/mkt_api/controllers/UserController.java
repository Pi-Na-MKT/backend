package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.UserDTOs.*;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.security.SecurityUtils;
import com.pina.mkt_api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "1 - Users", description = "Gerenciamento de usuários e autenticação")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, JwtUtil jwtUtil, SecurityUtils securityUtils) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "ADMIN vê todos. Outros veem apenas membros de boards compartilhados.")
    public ResponseEntity<List<UserSummaryDTO>> getAll() {
        List<UserSummaryDTO> response = userService.findAllUsers().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Meu perfil", description = "Retorna o perfil completo do usuário autenticado")
    public ResponseEntity<UserResponseDTO> getMe() {
        User user = userService.findMe(securityUtils.getAuthenticatedEmail());
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna perfil completo. Requer ADMIN, GESTOR ou próprio usuário.")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Auto-cadastro com papel padrão USER")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(userService.register(requestDTO)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Criar usuário (admin)", description = "Cria usuário com papel definido. Requer ADMIN ou GESTOR.")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO requestDTO) {
        User user = new User();
        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setPassword(requestDTO.password());
        user.setPhone(requestDTO.phone());
        user.setJobTitle(requestDTO.jobTitle());
        user.setSeniority(requestDTO.seniority());
        user.setResponsibility(requestDTO.responsibility());
        user.setBio(requestDTO.bio());
        user.setLinkedin(requestDTO.linkedin());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(userService.createUser(user, requestDTO.roleId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') or @userService.isOwnProfile(#id, authentication.name)")
    @Operation(summary = "Atualizar usuário", description = "Atualiza perfil. Próprio usuário ou ADMIN/GESTOR.")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateData) {
        return ResponseEntity.ok(toDTO(userService.updateUser(id, updateData)));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Trocar senha", description = "Troca a senha do próprio usuário autenticado")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeDTO dto) {
        userService.changePassword(securityUtils.getAuthenticatedEmail(), dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Alterar papel do usuário", description = "Altera o papel de um usuário. Requer ADMIN.")
    public ResponseEntity<UserResponseDTO> changeRole(
            @PathVariable Long id,
            @Parameter(description = "ID do novo papel") @RequestParam Long roleId) {
        return ResponseEntity.ok(toDTO(userService.changeRole(id, roleId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Excluir usuário (Soft Delete)", description = "Desativa o usuário e remove de boards e cards. Requer ADMIN ou GESTOR.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginData) {
        User user = userService.login(loginData.email(), loginData.password());
        String role = user.getRole() != null ? user.getRole().getAccessKey() : "ROLE_USER";
        String token = jwtUtil.generateToken(user.getEmail(), role);

        return ResponseEntity.ok(new LoginResponseDTO(
                token, user.getId(), user.getName(),
                user.getRole() != null ? user.getRole().getName() : null
        ));
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getJobTitle(), user.getSeniority(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getAvatarUrl(), user.getBio(), user.getResponsibility(), user.getLinkedin()
        );
    }

    private UserSummaryDTO toSummaryDTO(User user) {
        return new UserSummaryDTO(user.getId(), user.getName(), user.getAvatarUrl(), user.getJobTitle());
    }
}
