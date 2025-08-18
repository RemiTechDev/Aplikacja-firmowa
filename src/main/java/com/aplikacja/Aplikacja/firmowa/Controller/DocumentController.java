package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.*;
import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.DocumentCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentCommentService documentCommentService;

    /** Widok użytkownika – jego pliki */
    @GetMapping("/documents")
    public String documents(Model model, Principal principal) {
        String login = principal != null ? principal.getName() : null;
        List<Document> docs = (login != null) ? documentRepository.findByUser_Login(login) : List.of();
        model.addAttribute("docs", docs);
        return "documents";
    }

    /** Panel admina – pełna lista */
    @GetMapping("/admin/documents")
    public String adminDocuments(Model model) {
        model.addAttribute("docs", documentRepository.findAllByOrderByCreatedDesc());
        return "admin_documents";
    }

    /** Upload (dostępny też z panelu admina) */
    @PostMapping("/documents/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Principal principal,
                         @RequestParam(value = "title", required = false) String title) throws IOException {
        if (file.isEmpty()) return "redirect:/admin/documents";

        Path uploadDir = Paths.get("uploads");
        if (Files.notExists(uploadDir)) Files.createDirectories(uploadDir);

        Path path = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        User user = (principal != null) ? userRepository.findByLogin(principal
                .getName()).orElse(null) : null;

        Document doc = new Document();
        doc.setTitle((title != null && !title.isBlank()) ? title : file.getOriginalFilename());
        doc.setFilePath(path.toString().replace('\\','/'));
        doc.setCreated(LocalDateTime.now());
        doc.setUser(user);
        doc.setMimeType(file.getContentType());
        doc.setStatus(DocumentStatus.AKTYWNY);

        // rozpoznanie typu na podstawie rozszerzenia
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            String ext = name.substring(name.lastIndexOf('.') + 1).toUpperCase();
            doc.setType(mapExtToType(ext));
        } else {
            doc.setType(DocumentType.TXT);
        }

        documentRepository.save(doc);
        return "redirect:/admin/documents";
    }

    private DocumentType mapExtToType(String ext) {
        switch (ext) {
            case "PDF": return DocumentType.PDF;
            case "DOC": return DocumentType.DOC;
            case "DOCX": return DocumentType.DOCX;
            case "XLS": return DocumentType.XLS;
            case "XLSX": return DocumentType.XLSX;
            case "CSV": return DocumentType.CSV;
            case "XML": return DocumentType.XML;
            case "PPT": return DocumentType.PPT;
            case "PPTX": return DocumentType.PPT; // mapujemy na PPT
            case "ODP": return DocumentType.ODP;
            case "PPS": return DocumentType.PPS;
            case "XLTX": return DocumentType.XLTX;
            case "RTF": return DocumentType.RTF;
            case "WPS": return DocumentType.WPS;
            case "WRI": return DocumentType.WRI;
            default: return DocumentType.TXT;
        }
    }

    /** Strona szczegółów + komentarze */
    @GetMapping("/documents/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return "redirect:/admin/documents";
        model.addAttribute("doc", doc);
        model.addAttribute("comments", documentCommentService.getComments(id));
        return "document_view";
    }

    /** Pobieranie/otwieranie pliku (inline) */
    @GetMapping("/documents/file/{id}")
    public ResponseEntity<Resource> openInline(@PathVariable Long id) throws IOException {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null || doc.getFilePath() == null) return ResponseEntity.notFound().build();

        Path path = Paths.get(doc.getFilePath());
        if (!Files.exists(path)) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(path.toUri());
        String contentType = doc.getMimeType();
        if (contentType == null) {
            contentType = Files.probeContentType(path);
        }
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""
                        + path.getFileName().toString() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /** Wymuszone pobranie */
    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null || doc.getFilePath() == null) return ResponseEntity.notFound().build();

        Path path = Paths.get(doc.getFilePath());
        if (!Files.exists(path)) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(path.toUri());
        String contentType = doc.getMimeType();
        if (contentType == null) {
            contentType = Files.probeContentType(path);
        }
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + path.getFileName().toString() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /** Dodanie komentarza (z dashboardu lub ze strony dokumentu) */
    @PostMapping("/documents/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal,
                             @RequestParam(value = "redirect", required = false) String redirect) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc != null) {
            String login = principal != null ? principal.getName() : null;
            documentCommentService.addComment(doc, login, content);
        }
        if ("view".equalsIgnoreCase(redirect)) {
            return "redirect:/documents/view/" + id;
        }
        return "redirect:/admin/documents";
    }

    /** Formularz edycji */
    @GetMapping("/documents/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return "redirect:/admin/documents";
        model.addAttribute("doc", doc);
        model.addAttribute("statuses", DocumentStatus.values());
        return "document_edit";
    }

    /** Zapis edycji (tytuł, status) */
    @PostMapping("/documents/edit")
    public String update(@RequestParam Long id,
                         @RequestParam String title,
                         @RequestParam(required = false) DocumentStatus status) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc != null) {
            doc.setTitle(title);
            if (status != null) doc.setStatus(status);
            documentRepository.save(doc);
        }
        return "redirect:/admin/documents";
    }

    /** Usuń */
    @PostMapping("/documents/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id) {
        // (opcjonalnie – gdyby kaskada z jakiegoś powodu nie zadziałała)
        documentCommentService.deleteAllByDocumentId(id);

        documentRepository.deleteById(id);
        return "redirect:/admin/documents";
    }
}