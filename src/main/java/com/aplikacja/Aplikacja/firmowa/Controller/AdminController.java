package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;
import com.aplikacja.Aplikacja.firmowa.Load.RequestLogin.Response.ResponseMessage;
import com.aplikacja.Aplikacja.firmowa.Mapper.UserMapper;
import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @PostMapping("/admin/newuser")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody UserDto userDto) {
        if (userRepository.existsByLogin(userDto.getLogin())) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error: Account with this name is taken"));
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error: Account with this email address is in use"));
        }
        userService.addNewUser(userMapper.mapToUser(userDto));

        return ResponseEntity.ok(new ResponseMessage("New account account is created successfully"));
    }

    @GetMapping("/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto getUser(@PathVariable Long id) {
        return userMapper.mapToUserDto(userService.findById(id));
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto>getAllUsers(){
        return userMapper.mapToUserDtoList(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id){
        userService.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUserInformation(@PathVariable Long id, @RequestBody UserDto userDto){
        User user= userService.findById(id);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setSignUpDate(userDto.getSignUpDate());
        return userMapper.mapToUserDto(userService.save(user));
    }

    @PutMapping("/roles/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity <?>  updateRolesAndStatus (@PathVariable Long id,
                                                      @RequestParam(required = false )List <String> roleNames,
                                                      @RequestParam(required = false) Boolean enabled){
        User user = userService.findById(id);
        if(roleNames != null && !roleNames.isEmpty()) {
            Set<Role> roles = roleNames.stream()
                    .map(role -> userService.findOrCreateRole(role))
                    .collect (Collectors.toSet());
            user.setRoles(roles);
        }
        if(enabled != null) {
            user.setEnabled(enabled);
        }
        userService.save(user);
        return ResponseEntity.ok(new ResponseMessage("Roles updated successfully"));
    }
}
