//package com.aplikacja.Aplikacja.firmowa.Controller;
//import com.aplikacja.Aplikacja.firmowa.Model.Document;
//import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Controller
//@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('ADMIN_ROLE','MANAGER_ROLE')")
//public class ManagerDocumentsController {
//
//    private final DocumentRepository documentRepository;
//
//    @GetMapping("/manager/documents")
//    public String list(Model model) {
//        List<Document> docs = documentRepository.findAll().stream()
//                .sorted(Comparator.comparing(Document::getCreated,
//                        Comparator.nullsLast(Comparator.reverseOrder())))
//                .collect(Collectors.toList());
//
//        model.addAttribute("docs", docs);
//        return "manager_documents";
//    }
//}


// LOGIKA CAŁA PRZENIESIONA DO MENAGERvIEWcONTROLLER