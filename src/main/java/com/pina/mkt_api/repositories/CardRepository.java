package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByColumnId(Long columnId);

    @Query("SELECT c FROM Card c WHERE c.column.board IN (SELECT b FROM Board b JOIN b.users u WHERE u.email = :email)")
    List<Card> findAccessibleByUserEmail(@Param("email") String email);

    @Query("SELECT COUNT(c) FROM Card c JOIN c.column col JOIN col.board b JOIN b.users u WHERE c.id = :cardId AND u.email = :email")
    long countCardAccessForUser(@Param("cardId") Long cardId, @Param("email") String email);

    @Modifying
    @Query(value = "DELETE FROM USER_CARD WHERE user_id = :userId", nativeQuery = true)
    void removeUserFromAllCards(@Param("userId") Long userId);
}
