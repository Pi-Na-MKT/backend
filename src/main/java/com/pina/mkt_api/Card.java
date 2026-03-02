package com.pina.mkt_api;

import java.time.LocalDateTime;

public class Card {

    private static Integer counter = 1; // Alterar quando o banco for criado para que a aplicação não resete quando for reiniciada
    private Integer id;

    private String title;
    private String description;

    private TaskStatus status; // Com ENUM, para um campo select no front
    private Priority priority; // Com ENUM, para um campo select no front
    private String company;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private LocalDateTime dueDate;

    public Card(String title, String description, TaskStatus status, Priority priority, String company, LocalDateTime updateAt, LocalDateTime dueDate) {
        this.id = counter++;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.company = company;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }

    public Card() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
