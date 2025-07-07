package com.aplikacja.Aplikacja.firmowa.Mapper;

import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;
import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public User mapToUser(UserDto userDto) {
        Set<String> strRoles = userDto.getRole();
        Set<Role> roles = new HashSet<>();

        checkRoles();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERoles.USER_ROLE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERoles.ADMIN_ROLE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                }
            });
        }
        return User.builder()
                .login(userDto.getLogin())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(encoder.encode(userDto.getPassword()))
                .roles(roles)
                .build();
    }

    private void checkRoles() {

        if (!(roleRepository.findByName(ERoles.USER_ROLE).isPresent())) {
            Role role = new Role(ERoles.USER_ROLE);
            roleRepository.save(role);

        }
        if (!(roleRepository.findByName(ERoles.ADMIN_ROLE).isPresent())) {
            Role role = new Role(ERoles.ADMIN_ROLE);
            roleRepository.save(role);
        }
    }

    public UserDto mapToUserDto(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new UserDto(
                user.getLogin(),
                user.getFirstName(),

                user.getLastName(),
                user.getEmail(),
                roles,
                user.getPassword(),
                user.getSignUpDate());
    }

    public List<UserDto> mapToUserDtoList(List<User> userList) {
        return userList.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }
}