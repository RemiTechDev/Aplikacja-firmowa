package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.DocumentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentCommentRepository extends JpaRepository<DocumentComment, Long> {
    List<DocumentComment> findByDocument_IdOrderByCreatedAtAsc(Long documentId);
    void deleteByDocument_Id(Long documentId);

}