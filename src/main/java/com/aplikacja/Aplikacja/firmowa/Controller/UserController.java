package com.aplikacja.Aplikacja.firmowa.Controller;


import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;
import com.aplikacja.Aplikacja.firmowa.Load.RequestLogin.RequiredLogin;
import com.aplikacja.Aplikacja.firmowa.Load.RequestLogin.Response.JWebTokenResponse;
import com.aplikacja.Aplikacja.firmowa.Load.RequestLogin.Response.ResponseMessage;
import com.aplikacja.Aplikacja.firmowa.Mapper.UserMapper;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.UserService;
import com.aplikacja.Aplikacja.firmowa.Validation.ValidPassword;
import com.aplikacja.Aplikacja.firmowa.security.JWebToken.JWebTokenUtils;
import com.aplikacja.Aplikacja.firmowa.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWebTokenUtils jWebTokenUtils;

    @PostMapping("/authorize/login")
    public ResponseEntity<?> authorizeUser(@Valid @RequestBody RequiredLogin requiredLogin) {
        UsernamePasswordAuthenticationToken loginAndPasswordAuth =
                new UsernamePasswordAuthenticationToken(requiredLogin.getLogin(),
                        requiredLogin.getPassword());

        Authentication authentication = authenticationManager
                .authenticate(loginAndPasswordAuth);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jWebToken = jWebTokenUtils.generateJWebToken(authentication);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        List<String> role = userDetailsImpl.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return ResponseEntity.ok(new JWebTokenResponse(jWebToken, userDetailsImpl.getId(),
                userDetailsImpl.getUsername(), userDetailsImpl.getEmail(), role));
    }


    // Rejestracja działa, ale wynik ejst zwracany w konsoli jako JSON
    @PostMapping("/authorize/register")
    public ResponseEntity<?> registerNewAdmin(@Valid @RequestBody UserDto userDto,
    org.springframework.validation.BindingResult result) {

        //Obsługa błędów podejście numer 1
//        if(result.hasErrors()){
//            String message = result.getAllErrors().get(0).getDefaultMessage();
//            return ResponseEntity.badRequest().body(new ResponseMessage("Error: Inncorect password type - "
//                    + message));
//
//        }

        // Obsługa błędów podejście numer 2
        if (result.hasErrors()) {
            List<String> messages = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(messages);
        }


            if (userRepository.existsByLogin(userDto.getLogin())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Error: This username is taken"));
            }
            if (userRepository.existsByEmail(userDto.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Error: this email address is in use"));
            }
            userService.addNewUser(userMapper.mapToUser(userDto));

            return ResponseEntity.ok(new ResponseMessage("New admin account is created successfully"));
        }


    // Wyświetlanie informacji o zalogowanym użytkowniku
    public ResponseEntity<?> getActualUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Zwracanie informacji o zalogowanym użytkowniku jako JSON a nie DTO (łatwiejsza forma.
        // Bez stosowania dodatkowych interfejsów) Może w przyszłosci będzie zmienione
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("id", userDetails.getId());
            put("login", userDetails.getUsername());
            put("firstName", userDetails.getFirstName());
            put("lastName", userDetails.getLastName());
            put("email", userDetails.getEmail());
            put("roles", roles);
        }});
    }
}

