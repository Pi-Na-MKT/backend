package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsBySlug(String slug);
}