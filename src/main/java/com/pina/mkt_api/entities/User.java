package com.pina.mkt_api.entities;

import com.pina.mkt_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
// @Schema descreve a entidade para aparecer no Swagger como modelo de dados
@Schema(name = "User", description = "Representa um usuário do sistema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do usuário", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome completo do usuário", example = "Maria Silva")
    private String name;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email do usuário (único)", example = "maria.silva@email.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Papel do usuário no sistema", example = "ADMIN")
    private Role role;

    @Column(nullable = false)
    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
