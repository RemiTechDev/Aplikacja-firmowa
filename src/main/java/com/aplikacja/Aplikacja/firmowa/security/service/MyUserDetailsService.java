package com.aplikacja.Aplikacja.firmowa.security.service;

import com.aplikacja.Aplikacja.firmowa.Model.Privilege;
import com.aplikacja.Aplikacja.firmowa.Model.Role;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("userDetailsService")
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final MessageSource messages;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return new org.springframework.security.core.userdetails.User(
                    " ", " ", true, true, true, true,
                    getAuthorities(Collections.singleton(
                            roleRepository.findByName(com.aplikacja.Aplikacja.firmowa.Model.ERoles.USER_ROLE)
                                    .orElseThrow(() -> new RuntimeException("Default role not found")))
                    )
            );
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                getAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {
        List<String> privileges = new ArrayList<>();
        List<Privilege> allPrivileges = new ArrayList<>();

        for (Role role : roles) {
            privileges.add(role.getName().name()); // Używamy enum ERoles
            if (role.getPrivileges() != null) {
                allPrivileges.addAll(role.getPrivileges());
            }
        }

        for (Privilege priv : allPrivileges) {
            privileges.add(priv.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String priv : privileges) {
            authorities.add(new SimpleGrantedAuthority(priv));
        }
        return authorities;
    }
}
