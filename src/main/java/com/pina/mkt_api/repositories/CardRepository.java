package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
}