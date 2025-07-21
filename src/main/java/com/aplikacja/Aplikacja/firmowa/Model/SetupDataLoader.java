package com.aplikacja.Aplikacja.firmowa.Model;

import com.aplikacja.Aplikacja.firmowa.Repositories.PrivelegeRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivelegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound(ERoles.ADMIN_ROLE.name(), adminPrivileges);
        createRoleIfNotFound(ERoles.USER_ROLE.name(), Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName(ERoles.ADMIN_ROLE).orElseGet(() -> {
            Role role = new Role(ERoles.ADMIN_ROLE); //zmiana logiki. funkcja pobiera rolę kiedy ta istnieje
            return roleRepository.save(role);        // lub tworzy nową kiedy nie istnieje
        });

        if (!userRepository.existsByLogin("admin1")) {
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Główny");
            user.setLogin("admin1");
            user.setPassword(passwordEncoder.encode("AdminGlowny123!"));
            user.setEmail("admin1@firma.pl");
            user.setRoles(Set.of(adminRole));
            user.setEnabled(true);
            userRepository.save(user);

            alreadySetup = true;
        }
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name).orElseGet(() ->privilegeRepository.
                save(new Privilege(name)));
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        ERoles roleEnum = ERoles.valueOf(name);
        Role role = roleRepository.findByName(roleEnum)
                .orElseGet(() -> {
                    Role newRole = new Role(roleEnum);
                    newRole.setPrivileges(privileges);
                    return roleRepository.save(newRole);
                });
        return role;
    }
}