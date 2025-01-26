package com.foroescolar.repository;

import com.foroescolar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {//GenericRepository<User, Long>{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);


}
