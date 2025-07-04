package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Document;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DocumentController {

    @Autowired private DocumentRepository documentRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/documents")
    public String documents(Model model, Principal principal) {
        List<Document> docs = documentRepository.findByUser_Login(principal.getName());
        model.addAttribute("docs", docs);
        return "index";
    }

    @PostMapping("/documents/upload")
    public String upload(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        Path path = Paths.get("uploads/" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        if (user != null) {
            Document doc = Document.builder()
                    .title(file.getOriginalFilename())
                    .filePath(path.toString())
                    .created(LocalDateTime.now())
                    .user(user)
                    .build();
            documentRepository.save(doc);
        }

        return "redirect:/documents";
    }
}
