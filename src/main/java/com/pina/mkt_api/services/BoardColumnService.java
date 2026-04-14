package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    public BoardColumnService(BoardColumnRepository columnRepository, BoardRepository boardRepository) {
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
    }

    public BoardColumn create(Long boardId, BoardColumn column) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("Board não encontrado"));
        column.setBoard(board);

        return columnRepository.save(column);
    }

    public List<BoardColumn> getByBoard(Long boardId) {
        return columnRepository.findByBoardId(boardId);
    }

    public void deleteColumn(Long id) {
        BoardColumn column = columnRepository.findById(id).orElseThrow(() -> new RuntimeException("Coluna não encontrada para deleção."));

        columnRepository.delete(column);
    }
}
