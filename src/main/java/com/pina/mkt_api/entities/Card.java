package com.pina.mkt_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pina.mkt_api.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
// @Schema descreve a entidade para aparecer no Swagger como modelo de dados
@Schema(name = "Card", description = "Representa um card dentro de uma coluna")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do card", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "column_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("cards")
    @Schema(description = "Coluna à qual o card pertence")
    private BoardColumn column;

    @Column(nullable = false)
    @Schema(description = "Posição do card dentro da coluna", example = "1")
    private Integer position = 0;

    @Column(nullable = false)
    @Schema(description = "Título do card", example = "Campanha de Marketing")
    private String title;

    @Column(nullable = false, length = 1000)
    @Schema(description = "Descrição detalhada do card", example = "Planejar campanha de redes sociais")
    private String description;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Prioridade do card", example = "HIGH")
    private Priority priority;

    @Column(nullable = false)
    @Schema(description = "Empresa associada ao card", example = "Microsoft")
    private String company;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "Data de criação do card")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Data da última atualização do card")
    private LocalDateTime updatedAt;

    @Schema(description = "Data de vencimento do card")
    private LocalDateTime dueDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BoardColumn getColumn() { return column; }
    public void setColumn(BoardColumn column) { this.column = column; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}