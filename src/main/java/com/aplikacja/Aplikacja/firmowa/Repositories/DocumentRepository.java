package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.Document;
import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findDocumentByTitle(String title);

    List<Document> findByUser_Login(String login);
    long countByUser_Login(String login);

    List<Document> findAllByOrderByCreatedDesc();
    List<Document> findTop2ByOrderByCreatedDesc();

    //  USER ma widzieć tylko dokumenty wrzucone przez ADMINA
    @Query("""
           SELECT d FROM Document d
           JOIN d.user u
           JOIN u.roles r
           WHERE r.name = :role
           ORDER BY d.created DESC
           """)
    List<Document> findAllByUploaderRole(@Param("role") ERoles role);

    // (opcjonalnie) przyda się do sprawdzania, czy uploader ma rolę STAFF
    @Query("""
           SELECT COUNT(d) FROM Document d
           JOIN d.user u
           JOIN u.roles r
           WHERE d.id = :docId AND r.name = :role
           """)
    long countDocUploaderHasRole(@Param("docId") Long docId, @Param("role") ERoles role);
}
