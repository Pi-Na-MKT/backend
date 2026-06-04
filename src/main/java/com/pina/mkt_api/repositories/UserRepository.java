package com.pina.mkt_api.repositories;

import com.pina.mkt_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByIsActiveTrue();

    @Query("SELECT DISTINCT u FROM User u JOIN u.boards b JOIN b.users me WHERE me.email = :email AND u.isActive = true")
    List<User> findMembersInSharedBoards(@Param("email") String email);
}
