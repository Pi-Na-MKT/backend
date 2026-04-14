package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}