package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsBySlug(String slug);

    @Query("SELECT DISTINCT b.company FROM Board b JOIN b.users u WHERE u.email = :email")
    List<Company> findAccessibleByUserEmail(@Param("email") String email);

    @Query("SELECT COUNT(b) FROM Board b JOIN b.users u WHERE b.company.id = :companyId AND u.email = :email")
    long countBoardsInCompanyForUser(@Param("companyId") Long companyId, @Param("email") String email);
}
