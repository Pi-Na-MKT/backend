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
@Tag(name = "Users", description = "Gerenciamento de usuários e autenticação")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> response = userService.findAllUsers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> getById(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    public ResponseEntity<UserResponseDTO> register(
            @Parameter(description = "Dados do usuário") @RequestBody UserRequestDTO requestDTO) {

        User user = new User();
        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setPassword(requestDTO.password());

        User savedUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedUser));
    }

    @PostMapping
    @Operation(summary = "Criar usuário (admin)", description = "Cria um novo usuário via API administrativa")
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO requestDTO) {
        User user = new User();
        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setPassword(requestDTO.password());

        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    public ResponseEntity<UserResponseDTO> update(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @RequestBody UserRequestDTO requestDTO) {

        User user = new User();
        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setPassword(requestDTO.password());

        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário pelo seu ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Realiza login e retorna um token JWT")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody UserRequestDTO loginData) {

        User user = userService.login(loginData.email(), loginData.password());
        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive()
        );
    }
}