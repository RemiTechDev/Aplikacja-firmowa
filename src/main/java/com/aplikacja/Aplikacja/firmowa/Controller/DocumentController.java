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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
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
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentCommentService documentCommentService;

    // =========================
    // USER: /documents -> tylko dokumenty ADMINA
    // =========================
    @GetMapping("/documents")
    public String userDocuments(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

        // jeśli to nie USER, niech idzie do swojego panelu (admin/manager/staff mają swoje ścieżki)
        if (!roles.contains("ROLE_USER_ROLE")) return "redirect:/panel";

        List<Document> docs = documentRepository.findAllByUploaderRole(ERoles.ADMIN_ROLE);
        model.addAttribute("docs", docs);
        return "user_documents";
    }

    // =========================
    // ADMIN: pełna lista
    // =========================
    @GetMapping("/admin/documents")
    public String adminDocuments(Model model) {
        model.addAttribute("docs", documentRepository.findAllByOrderByCreatedDesc());
        return "admin_documents";
    }

    // =========================
    // Upload – tylko ADMIN i MANAGER i STAFF
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN_ROLE','MANAGER_ROLE','STAFF_ROLE')")
    @PostMapping("/documents/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Principal principal,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "redirectTo", required = false, defaultValue = "/panel")
                         String redirectTo)
            throws IOException {

        if (file.isEmpty()) return "redirect:" + redirectTo;

        Path uploadDir = Paths.get("uploads");
        if (Files.notExists(uploadDir)) Files.createDirectories(uploadDir);

        Path path = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        User user = (principal != null)
                ? userRepository.findByLogin(principal.getName()).orElse(null)
                : null;

        Document doc = new Document();
        doc.setTitle((title != null && !title.isBlank()) ? title : file.getOriginalFilename());
        doc.setFilePath(path.toString().replace('\\', '/'));
        doc.setCreated(LocalDateTime.now());
        doc.setUser(user);
        doc.setMimeType(file.getContentType());
        doc.setStatus(DocumentStatus.AKTYWNY);

        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            String ext = name.substring(name.lastIndexOf('.') + 1).toUpperCase();
            doc.setType(mapExtToType(ext));
        } else {
            doc.setType(DocumentType.TXT);
        }

        documentRepository.save(doc);
        return "redirect:" + redirectTo;
    }

    private DocumentType mapExtToType(String ext) {
        return switch (ext) {
            case "PDF" -> DocumentType.PDF;
            case "DOC" -> DocumentType.DOC;
            case "DOCX" -> DocumentType.DOCX;
            case "XLS" -> DocumentType.XLS;
            case "XLSX" -> DocumentType.XLSX;
            case "CSV" -> DocumentType.CSV;
            case "XML" -> DocumentType.XML;
            case "PPT" -> DocumentType.PPT;
            case "PPTX" -> DocumentType.PPT; // mapujemy na PPT
            case "ODP" -> DocumentType.ODP;
            case "PPS" -> DocumentType.PPS;
            case "XLTX" -> DocumentType.XLTX;
            case "RTF" -> DocumentType.RTF;
            case "WPS" -> DocumentType.WPS;
            case "WRI" -> DocumentType.WRI;
            default -> DocumentType.TXT;
        };
    }

    // =========================
    // View + komentarze (wszyscy zalogowani)
    // USER powinien móc otwierać tylko dokumenty ADMINA -> blokujemy w backendzie
    // =========================
    @GetMapping("/documents/view/{id}")
    public String view(@PathVariable Long id, Model model,
                       Authentication auth,
                       @RequestParam(value = "back", required = false, defaultValue = "/panel") String back) {

        if (auth == null) return "redirect:/login";

        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return "redirect:" + back;

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

        //  USER: tylko dokumenty wrzucone przez ADMINA
        if (roles.contains("ROLE_USER_ROLE") && !roles.contains("ROLE_ADMIN_ROLE")) {
            boolean uploaderIsAdmin = documentRepository.countDocUploaderHasRole(doc.getId(), ERoles.ADMIN_ROLE) > 0;
            if (!uploaderIsAdmin) return "redirect:/documents?err=Brak+dostępu";
        }

        model.addAttribute("doc", doc);
        model.addAttribute("comments", documentCommentService.getComments(id));
        model.addAttribute("back", back);
        return "document_view";
    }

    // =========================
    // Inline/Pobierz – też blokujemy USER poza adminowymi
    // =========================
    @GetMapping("/documents/file/{id}")
    public ResponseEntity<Resource> openInline(@PathVariable Long id, Authentication auth) throws IOException {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null || doc.getFilePath() == null) return ResponseEntity.notFound().build();

        if (auth == null) return ResponseEntity.status(401).build();

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (roles.contains("ROLE_USER_ROLE") && !roles.contains("ROLE_ADMIN_ROLE")) {
            boolean uploaderIsAdmin = documentRepository.countDocUploaderHasRole(doc.getId(), ERoles.ADMIN_ROLE) > 0;
            if (!uploaderIsAdmin) return ResponseEntity.status(403).build();
        }

        Path path = Paths.get(doc.getFilePath());
        if (!Files.exists(path)) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(path.toUri());
        String contentType = doc.getMimeType();
        if (contentType == null) contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id, Authentication auth) throws IOException {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null || doc.getFilePath() == null) return ResponseEntity.notFound().build();

        if (auth == null) return ResponseEntity.status(401).build();

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (roles.contains("ROLE_USER_ROLE") && !roles.contains("ROLE_ADMIN_ROLE")) {
            boolean uploaderIsAdmin = documentRepository.countDocUploaderHasRole(doc.getId(), ERoles.ADMIN_ROLE) > 0;
            if (!uploaderIsAdmin) return ResponseEntity.status(403).build();
        }

        Path path = Paths.get(doc.getFilePath());
        if (!Files.exists(path)) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(path.toUri());
        String contentType = doc.getMimeType();
        if (contentType == null) contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // =========================
    // Komentarz – USER może komentować tylko adminowe
    // =========================
    @PostMapping("/documents/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal,
                             Authentication auth,
                             @RequestParam(value = "redirectTo", required = false, defaultValue = "/panel") String redirectTo) {

        if (auth == null) return "redirect:/login";

        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return "redirect:" + redirectTo;

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

        if (roles.contains("ROLE_USER_ROLE") && !roles.contains("ROLE_ADMIN_ROLE")) {
            boolean uploaderIsAdmin = documentRepository.countDocUploaderHasRole(doc.getId(), ERoles.ADMIN_ROLE) > 0;
            if (!uploaderIsAdmin) return "redirect:/documents?err=Brak+dostępu";
        }

        String login = principal != null ? principal.getName() : null;
        documentCommentService.addComment(doc, login, content);
        return "redirect:" + redirectTo;
    }

    // =========================
    // Edit – tylko ADMIN/MANAGER (jak wcześniej, ale backendowo)
    // =========================
    @GetMapping("/documents/edit/{id}")
    public String edit(@PathVariable Long id, Model model,
                       Authentication auth,
                       @RequestParam(value = "back", required = false, defaultValue = "/panel") String back) {

        if (auth == null) return "redirect:/login";

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (!(roles.contains("ROLE_ADMIN_ROLE") || roles.contains("ROLE_MANAGER_ROLE"))) {
            return "redirect:" + back + "?err=Brak+uprawnień";
        }

        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return "redirect:" + back;

        model.addAttribute("doc", doc);
        model.addAttribute("statuses", DocumentStatus.values());
        model.addAttribute("back", back);
        return "document_edit";
    }

    @PostMapping("/documents/edit")
    public String update(@RequestParam Long id,
                         @RequestParam String title,
                         @RequestParam(required = false) DocumentStatus status,
                         Authentication auth,
                         @RequestParam(value = "redirectTo", required = false, defaultValue = "/panel") String redirectTo) {

        if (auth == null) return "redirect:/login";

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (!(roles.contains("ROLE_ADMIN_ROLE") || roles.contains("ROLE_MANAGER_ROLE"))) {
            return "redirect:" + redirectTo + "?err=Brak+uprawnień";
        }

        Document doc = documentRepository.findById(id).orElse(null);
        if (doc != null) {
            doc.setTitle(title);
            if (status != null) doc.setStatus(status);
            documentRepository.save(doc);
        }
        return "redirect:" + redirectTo;
    }

    // =========================
    // DELETE – ADMIN: wszystko
    // MANAGER: tylko swoje + STAFF
    // =========================
    @PostMapping("/documents/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         Authentication auth,
                         @RequestParam(value = "redirectTo", required = false, defaultValue = "/panel") String redirectTo) {

        if (auth == null) return "redirect:/login";

        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return "redirect:" + redirectTo;

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

        // ADMIN może wszystko
        if (roles.contains("ROLE_ADMIN_ROLE")) {
            documentCommentService.deleteAllByDocumentId(id);
            documentRepository.deleteById(id);
            return "redirect:" + redirectTo;
        }

        // MANAGER: tylko swoje + STAFF
        if (roles.contains("ROLE_MANAGER_ROLE")) {
            String me = auth.getName();
            boolean isMine = doc.getUser() != null && me.equals(doc.getUser().getLogin());
            boolean uploaderIsStaff = documentRepository.countDocUploaderHasRole(doc.getId(), ERoles.STAFF_ROLE) > 0;

            if (isMine || uploaderIsStaff) {
                documentCommentService.deleteAllByDocumentId(id);
                documentRepository.deleteById(id);
                return "redirect:" + redirectTo;
            }

            return "redirect:" + redirectTo + "?err=Nie+możesz+usunąć+tego+dokumentu";
        }

        // STAFF/USER: brak
        return "redirect:" + redirectTo + "?err=Brak+uprawnień";
    }
}
