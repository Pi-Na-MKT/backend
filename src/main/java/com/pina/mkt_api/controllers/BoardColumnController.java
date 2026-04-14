package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.services.BoardColumnService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/columns")
@CrossOrigin(origins = "*")
public class BoardColumnController {

    private final BoardColumnService service;

    public BoardColumnController(BoardColumnService service) {
        this.service = service;
    }

    @PostMapping("/board/{boardId}")
    public BoardColumn create(@PathVariable Long boardId, @RequestBody BoardColumn column) {
        return service.create(boardId, column);
    }

    @GetMapping("/board/{boardId}")
    public List<BoardColumn> getByBoard(@PathVariable Long boardId) {
        return service.getByBoard(boardId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteColumn(id);
    }
}