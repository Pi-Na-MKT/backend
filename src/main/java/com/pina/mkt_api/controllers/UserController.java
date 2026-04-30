package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.UserDTOs.UserRequestDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserResponseDTO;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.security.JwtUtil;
import com.pina.mkt_api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "1 - Users", description = "Gerenciamento de usuários e autenticação")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    @Operation(summary = "Listar usuários ativos", description = "Retorna todos os usuários cadastrados e ativos")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> response = userService.findAllUsers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Auto-cadastro de usuário (recebe cargo padrão USER)")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserRequestDTO requestDTO) {

        User savedUser = userService.register(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDTO(savedUser));
    }

    @PostMapping
    @Operation(summary = "Criar usuário (admin)", description = "Cria um novo usuário definindo o Cargo (Role)")
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserRequestDTO requestDTO) {

        User user = new User();
        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setPassword(requestDTO.password());

        user.setPhone(requestDTO.phone());
        user.setJobTitle(requestDTO.jobTitle());
        user.setDepartment(requestDTO.department());
        user.setSeniority(requestDTO.seniority());

        user.setResponsibility(requestDTO.responsibility());
        user.setBio(requestDTO.bio());
        user.setLinkedin(requestDTO.linkedin());

        User savedUser = userService.createUser(user, requestDTO.roleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO requestDTO) {

        User user = new User();

        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setPassword(requestDTO.password());
        user.setPhone(requestDTO.phone());
        user.setJobTitle(requestDTO.jobTitle());
        user.setDepartment(requestDTO.department());
        user.setSeniority(requestDTO.seniority());
        user.setResponsibility(requestDTO.responsibility());
        user.setBio(requestDTO.bio());
        user.setLinkedin(requestDTO.linkedin());

        User updatedUser = userService.updateUser(id, user, requestDTO.roleId());

        return ResponseEntity.ok(toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário (Soft Delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Realiza login e retorna um token JWT")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody UserRequestDTO loginData) {
        User user = userService.login(loginData.email(), loginData.password());
        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("role", user.getRole() != null ? user.getRole().getName() : null);

        return ResponseEntity.ok(response);
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getJobTitle(),
                user.getSeniority(),
                user.getDepartment(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getAvatarUrl(),
                user.getBio(),
                user.getResponsibility(),
                user.getLinkedin()
        );
    }
}