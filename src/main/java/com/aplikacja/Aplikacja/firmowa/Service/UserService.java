package com.aplikacja.Aplikacja.firmowa.Service;

import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Service.exceptions.UserExistException;
import com.aplikacja.Aplikacja.firmowa.Service.exceptions.UserNotExistException;
import org.springframework.stereotype.Service;
import com.aplikacja.Aplikacja.firmowa.Model.Role;


import java.util.List;


public interface UserService {
    User save(User user);

    // od teraz jest to funkcja pomocnicza ze względu na swoje ograniczenia. Sprawdza imię i nazwisko.
    User addNewUser(User user) throws UserExistException;

    void deleteById(Long id) throws UserNotExistException;
    List<User> getAllUsers();
    User findById(Long id) throws UserNotExistException;

    //Przypisanie z automatu roli User. Szyfrowanie hasła. Sprawdzanie czy login i mail są unikalne
    //(użyte tylko raz).
    User registerNewUser(UserDto accountDto) throws UserExistException;

    Role findOrCreateRole(String roleName);


}
