package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//Klasa odpowiedzialna za zwrot widoków html

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN_ROLE')")
public class AdminViewController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    //  Widok listy użytkowników
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin_users"; // <- plik HTML: src/main/resources/templates/admin_users.html
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

        return "admin_user_edit"; // <- plik HTML: src/main/resources/templates/admin_user_edit.html
    }

    //  Obsługa formularza - aktualizacja ról i statusu aktywności
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             @RequestParam(value = "enabled", required = false) boolean enabled) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "redirect:/admin/users";
        }

        User user = optionalUser.get();
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
                             @RequestParam Long roleId) {

        Role role = roleRepository.findById(roleId).orElseThrow();

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        userRepository.save(user);
        return "redirect:/admin/users";
    }


}
