package com.aplikacja.Aplikacja.firmowa.Service;

import com.aplikacja.Aplikacja.firmowa.Model.Document;
import com.aplikacja.Aplikacja.firmowa.Model.DocumentComment;
import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentCommentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentCommentService {

    private final DocumentCommentRepository repo;
    private final UserRepository userRepository;

    public List<DocumentComment> getComments(Long documentId) {
        // Jeśli potrzebujesz sortowania – dodaj w repo metodę findByDocument_IdOrderByCreatedAtAsc
        return repo.findAll(); // uproszczone; dopasuj do swojej implementacji
    }

    public void addComment(Document doc, String authorLogin, String content) {
        DocumentComment c = new DocumentComment();
        c.setDocument(doc);
        c.setAuthor(authorLogin != null ? userRepository.findByLogin(authorLogin).orElse(null) : null);
        c.setContent(content);
        c.setCreatedAt(LocalDateTime.now());
        repo.save(c);
    }

    public void deleteAllByDocumentId(Long documentId) {
        repo.deleteByDocument_Id(documentId);
    }
}
