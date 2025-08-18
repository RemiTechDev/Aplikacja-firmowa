package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Override
    <S extends Document> S save(S document);

    @Override
    void deleteById(Long id);

    @Override
    List<Document> findAll();

    @Override
    Optional<Document> findById(Long id);

    Optional<Document> findDocumentByTitle(String title);

    List<Document> findByUser_Login(String login);
    long countByUser_Login(String login);

    // DODANE – używane na widokach
    List<Document> findAllByOrderByCreatedDesc();
    List<Document> findTop2ByOrderByCreatedDesc();
}
