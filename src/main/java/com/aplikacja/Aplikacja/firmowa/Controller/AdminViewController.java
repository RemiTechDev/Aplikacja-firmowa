package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.LoginHistory;
import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.*;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN_ROLE')")
public class AdminViewController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final DocumentRepository documentRepository;
    private final MeetingRepository meetingRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    //  Widok listy użytkowników
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin_users";
    }

    // Formularz edycji użytkownika
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "redirect:/admin/users";
        }
        User user = optionalUser.get();
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin_user_edit";
    }

    //  Aktualizacja ról / statusu / hasła
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             @RequestParam(value = "enabled", required = false) boolean enabled,
                             @RequestParam(value = "newPassword", required = false) String newPassword) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "redirect:/admin/users";
        }
        User user = optionalUser.get();

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        Set<Role> selectedRoles = new HashSet<>();
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                roleRepository.findById(roleId).ifPresent(selectedRoles::add);
            }
        }

        user.setRoles(selectedRoles);
        user.setEnabled(enabled);
        userRepository.save(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "admin_user_create";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String login,
                             @RequestParam String password,
                             @RequestParam String email,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             @RequestParam(value = "enabled", defaultValue = "false") boolean enabled) {

        Set<Role> selectedRoles = new HashSet<>();
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                roleRepository.findById(roleId).ifPresent(selectedRoles::add);
            }
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(enabled);
        user.setRoles(selectedRoles);
        user.setSignUpDate(LocalDateTime.now());

        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // Usuwanie użytkowników
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/dashboard/extended")
    public String dashboardView(Model model) {
        model.addAttribute("upcomingMeetings", meetingRepository.findTop5ByOrderByDateTimeAsc());
        model.addAttribute("latestDocuments", documentRepository.findTop2ByOrderByCreatedDesc());

        List<LoginHistory> preview = loginHistoryRepository.findTop5ByOrderByLoginTimeDesc();
        preview.forEach(entry -> {
            if (entry.getName() == null || entry.getName().isBlank()) {
                entry.setName(entry.getUser() != null
                        ? entry.getUser().getFirstName() + " " + entry.getUser().getLastName()
                        : "Nieznany użytkownik");
            }
            if (entry.getRole() == null || entry.getRole().isBlank()) {
                entry.setRole(entry.getUser() != null && !entry.getUser().getRoles().isEmpty()
                        ? entry.getUser().getRoles().iterator().next().getName().name()
                        : "BRAK");
            }
        });

        model.addAttribute("loginHistoryPreview", preview);
        model.addAttribute("loginHistoryTotal", loginHistoryRepository.count());

        return "admin_dashboard";
    }
}
