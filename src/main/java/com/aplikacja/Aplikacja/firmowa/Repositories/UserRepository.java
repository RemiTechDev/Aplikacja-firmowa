package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    <S extends User> S save(S user);

    @Override
    void deleteById(Long id);

    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(Long id);

    Optional<User>findByFirstNameAndLastName(String firstName, String lastName);

    Optional <User>findByLogin(String login);

    //Optional<User>findByLoginAndEmail(String login, String email);

    Optional<User> findByEmail(String email);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.login = :login")
    Set<Role> findRolesByLogin(@Param("login") String login);

}