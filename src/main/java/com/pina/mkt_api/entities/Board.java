package com.pina.mkt_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.List;

@Entity
// @Schema descreve a entidade para aparecer no Swagger como modelo de dados
@Schema(name = "Board", description = "Representa um board dentro do sistema")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Schema descreve o campo e pode incluir exemplos
    @Schema(description = "Identificador único do board", example = "1")
    private Long id;

    // @Schema adiciona descrição e exemplo para o campo "name"
    @Schema(description = "Nome do board", example = "Projeto Marketing")
    private String name;

    @ManyToOne
    // @Schema descreve a relação do board com o usuário
    @Schema(description = "Usuário dono do board")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    // @Schema descreve a lista de colunas associadas ao board
    @Schema(description = "Lista de colunas pertencentes ao board")
    private List<BoardColumn> columns;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<BoardColumn> getColumns() { return columns; }
    public void setColumns(List<BoardColumn> columns) { this.columns = columns; }
}