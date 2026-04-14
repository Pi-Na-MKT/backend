package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.services.UserService;
import com.pina.mkt_api.security.JwtUtil; // Importação da nossa classe de segurança
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Importação para manipular status HTTP
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil; // Injetando a classe que fabrica os Tokens

    // Essa rota agora está PROTEGIDA pelo JWT (você só consegue buscar a lista se mandar o token)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    // Mapeamos para /register para combinar com a liberação no SecurityConfig
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User novoUsuario = userService.createUser(user);
        return ResponseEntity.status(201).body(novoUsuario);
    }

    // A Rota Mágica do Login (Totalmente Liberada)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        try {
            User user = userService.login(loginData.getEmail(), loginData.getPassword());
            String token = jwtUtil.generateToken(user.getEmail());

            java.util.Map<String, Object> responseBody = new java.util.HashMap<>();
            responseBody.put("token", token);
            responseBody.put("userId", user.getId());
            responseBody.put("name", user.getName());

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }
}