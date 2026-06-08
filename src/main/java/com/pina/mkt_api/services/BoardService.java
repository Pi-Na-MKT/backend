package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.repositories.CompanyRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public BoardService(BoardRepository boardRepository, CompanyRepository companyRepository,
                        UserRepository userRepository, SecurityUtils securityUtils) {
        this.boardRepository = boardRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public Board create(Long companyId, Board board, List<Long> userIds) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        board.setCompany(company);

        if (userIds != null && !userIds.isEmpty()) {
            List<User> users = userRepository.findAllById(userIds);
            board.setUsers(users);
        }

        return boardRepository.save(board);
    }

    public List<Board> findAll() {
        if (securityUtils.isAdmin()) {
            return boardRepository.findAll();
        }
        return boardRepository.findByUsersEmail(securityUtils.getAuthenticatedEmail());
    }

    public Board findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board não encontrado"));

        if (!securityUtils.isAdmin() && !boardRepository.existsByIdAndUsersEmail(id, securityUtils.getAuthenticatedEmail())) {
            throw new ResourceNotFoundException("Board não encontrado");
        }

        return board;
    }

    public Board update(Long id, Board updatedBoard, List<Long> userIds) {
        Board existingBoard = findById(id);

        if (updatedBoard.getName() != null) existingBoard.setName(updatedBoard.getName());
        if (updatedBoard.getDescription() != null) existingBoard.setDescription(updatedBoard.getDescription());
        if (updatedBoard.getBackgroundColor() != null) existingBoard.setBackgroundColor(updatedBoard.getBackgroundColor());
        if (updatedBoard.getIsActive() != null) existingBoard.setIsActive(updatedBoard.getIsActive());

        if (userIds != null) {
            List<User> users = userRepository.findAllById(userIds);
            existingBoard.setUsers(users);
        }

        return boardRepository.save(existingBoard);
    }

    public void delete(Long id) {
        Board board = findById(id);
        boardRepository.delete(board);
    }
}
