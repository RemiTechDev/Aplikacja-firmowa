package com.aplikacja.Aplikacja.firmowa.Service.Implementation;

import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.UserService;
import com.aplikacja.Aplikacja.firmowa.Service.exceptions.UserExistException;
import com.aplikacja.Aplikacja.firmowa.Service.exceptions.UserNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
        return save(user);
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
}