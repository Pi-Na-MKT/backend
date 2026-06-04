package com.pina.mkt_api.services;

import com.pina.mkt_api.dtos.BoardColumnDTOs.BoardColumnUpdateDTO;
import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final SecurityUtils securityUtils;

    public BoardColumnService(BoardColumnRepository columnRepository, BoardRepository boardRepository,
                              SecurityUtils securityUtils) {
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
        this.securityUtils = securityUtils;
    }

    public BoardColumn create(Long boardId, BoardColumn column) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board não encontrado"));
        column.setBoard(board);
        return columnRepository.save(column);
    }

    public List<BoardColumn> getByBoard(Long boardId) {
        if (!securityUtils.isAdmin() &&
                !boardRepository.existsByIdAndUsersEmail(boardId, securityUtils.getAuthenticatedEmail())) {
            throw new ResourceNotFoundException("Board não encontrado");
        }
        return columnRepository.findByBoardId(boardId);
    }

    public BoardColumn updateColumn(Long id, BoardColumnUpdateDTO dto) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coluna não encontrada com o ID: " + id));

        if (dto.name() != null) column.setName(dto.name());
        if (dto.position() != null) column.setPosition(dto.position());

        return columnRepository.save(column);
    }

    public void deleteColumn(Long id) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coluna não encontrada para deleção."));
        columnRepository.delete(column);
    }
}
