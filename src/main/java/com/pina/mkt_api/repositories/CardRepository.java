package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findByCompanyIgnoreCase(String companyName);
}