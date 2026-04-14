package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.services.BoardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping
    public Board create(@RequestBody Board board) {
        return service.create(board);
    }

    @GetMapping
    public List<Board> getAll() {
        return service.findAll();
    }
}