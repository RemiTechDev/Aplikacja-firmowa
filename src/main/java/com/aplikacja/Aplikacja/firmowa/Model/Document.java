package com.aplikacja.Aplikacja.firmowa.Model;

import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_path")
    private String filePath;

    /** NOWE: typ pliku zgodny z Twoim enumem */
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocumentStatus status = DocumentStatus.AKTYWNY;

    @ElementCollection
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Relacja do komentarzy (kaskadowe usuwanie) */
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentComment> comments = new ArrayList<>();

    public Document() {}

    @Builder
    public Document(String title, String description, LocalDateTime created, String mimeType, String filePath,
                    DocumentStatus status, List<String> tags, User user, DocumentType type) {
        this.title = title;
        this.description = description;
        this.created = created;
        this.mimeType = mimeType;
        this.filePath = filePath;
        this.status = status;
        this.tags = tags;
        this.user = user;
        this.type = type;
    }

    // === gettery / settery ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<DocumentComment> getComments() { return comments; }
    public void setComments(List<DocumentComment> comments) { this.comments = comments; }

    @Override
    public String toString() {
        return "Document [id=" + id + ", title=" + title + ", desc=" + description + "]";
    }
}
