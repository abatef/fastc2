package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);

    boolean existsUserByPhone(String phone);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Integer id);

    Optional<User> getUserByFbUid(String fbUid);
}
