package com.aplikacja.Aplikacja.firmowa.Service.Implementation;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.UserService;
import com.aplikacja.Aplikacja.firmowa.Service.exceptions.UserExistException;
import com.aplikacja.Aplikacja.firmowa.Service.exceptions.UserNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User addNewUser(User user) throws UserExistException {
        if (userRepository.findByFirstNameAndLastName(user.getFirstName(),
                user.getLastName()).isPresent()) {
            throw new UserExistException(userRepository.findByFirstNameAndLastName(
                    user.getFirstName(), user.getLastName()).get().getId());
        }
        return registerNewUser(mapToDto(user));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.findById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) throws UserNotExistException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotExistException(id));
    }

    //Dodanie registerNewUser
    public User registerNewUser(UserDto accountDto) throws UserExistException {
        if (userRepository.existsByLogin(accountDto.getLogin())) {
            throw new UserExistException(null);
        }
        if (userRepository.existsByEmail(accountDto.getEmail())) {
            throw new UserExistException(null);
        }
        User user = new User();
        user.setLogin(accountDto.getLogin());
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setEmail(accountDto.getEmail());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));

        Role basicRole = roleRepository.findByName(ERoles.USER_ROLE)
                .orElseThrow(() -> new RuntimeException("Rola użytkownika nie została znaleziona"));

                Set<Role> roles = new HashSet<>();
                roles.add(basicRole);
                user.setRoles(roles);
                return userRepository.save(user);
    }
    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                user.getPassword()
        );
    }

}