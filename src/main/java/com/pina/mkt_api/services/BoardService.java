package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.Company;
import com.pina.mkt_api.repositories.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository repository;
    private final CompanyService companyService;

    public BoardService(BoardRepository repository, CompanyService companyService) {
        this.repository = repository;
        this.companyService = companyService;
    }

    public Board create(Long companyId, Board board) {
        Company company = companyService.findById(companyId);

        board.setCompany(company);

        return repository.save(board);
    }

    public List<Board> findAll() {
        return repository.findAll();
    }
}
