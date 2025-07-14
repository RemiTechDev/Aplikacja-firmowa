package com.aplikacja.Aplikacja.firmowa.Controller;


import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;
import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Controller
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("user") @Valid UserDto userDto,
            BindingResult bindingResult,
            Model model) {

        if (userRepository.existsByLogin(userDto.getLogin())) {
            bindingResult.rejectValue("login", "error.user", "Login jest już zajęty");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email jest już używany");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        // Mapowanie UserDto → User (ręcznie, bez mapera)
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setSignUpDate(LocalDateTime.now());
        user.setEnabled(true);

        Role defaultRole = roleRepository.findByName(ERoles.USER_ROLE)
                .orElseThrow(() -> new RuntimeException("Default role USER_ROLE not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}