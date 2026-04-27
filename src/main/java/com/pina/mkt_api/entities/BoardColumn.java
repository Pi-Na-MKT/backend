package com.pina.mkt_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.List;

@Entity
// @Schema descreve a entidade para aparecer no Swagger como modelo de dados
@Schema(name = "BoardColumn", description = "Representa uma coluna dentro de um board")
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Schema descreve o campo e pode incluir exemplos pr representar melhor
    @Schema(description = "Identificador único da coluna", example = "1")
    private Long id;

    // @Schema adiciona descrição e exemplo pro campo "name"
    @Schema(description = "Nome da coluna", example = "A Fazer")
    private String name;

    @Column(nullable = false)
    // @Schema descreve o campo "position" e mostra exemplo
    @Schema(description = "Posição da coluna no board", example = "1")
    private Integer position = 0;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIgnore
    // @Schema descreve a relação da coluna com o board
    @Schema(description = "Board ao qual a coluna pertence")
    private Board board;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    // @Schema descreve a lista de cards associados à coluna
    @Schema(description = "Lista de cards associados à coluna")
    private List<Card> cards;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }
}