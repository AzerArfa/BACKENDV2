package com.auth.repository;

import com.auth.entity.Entreprise;
import com.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByEmail(String email);
    Optional<User> findFirstByEmail(String email);
    Optional<User> findByName(String username);
    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByEntreprisesContains(Entreprise entreprise);
}
