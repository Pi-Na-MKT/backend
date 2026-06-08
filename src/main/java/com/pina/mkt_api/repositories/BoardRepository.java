package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT u.id FROM Board b JOIN b.users u WHERE b.id = :boardId")
    List<Long> findMemberIdsByBoardId(@Param("boardId") Long boardId);

    List<Board> findByUsersEmail(String email);

    boolean existsByIdAndUsersEmail(Long id, String email);

    @Modifying
    @Query(value = "DELETE FROM BOARD_USER WHERE user_id = :userId", nativeQuery = true)
    void removeUserFromAllBoards(@Param("userId") Long userId);
}
