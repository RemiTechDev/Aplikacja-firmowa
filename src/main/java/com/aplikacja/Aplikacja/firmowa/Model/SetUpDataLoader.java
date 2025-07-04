//package com.aplikacja.Aplikacja.firmowa.Model;
//
//import com.aplikacja.Aplikacja.firmowa.Repositories.PrivelegeRepository;
//import com.aplikacja.Aplikacja.firmowa.Repositories.RoleRepository;
//import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//@Component
//public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {
//
//    boolean alreadySetUp = false;
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private PrivelegeRepository privilegeReposytory;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        if (!alreadySetUp)
//            return;
//        Privilege readPrivilege = createPrivilegeIfNotFound("Read_Privilege");
//        Privilege writePrivilege= createPrivelegeIfNotFound("Write_Privelege");
//
//        List<Privilege> adminPriveleges = Arrays.asList(readPrivilege, writePrivilege);
//        createRoleIfNotFound("ADMIN_ROLE"),adminPriveleges;
//        createRoleIfNotFound("USER_ROLE"),Arrays.asList(readPrivilege))
//
//        Role adminRole = roleRepository.findByName("ADMIN_ROLE");
//        User user = new User();
//        user.setFirstName("John");
//        user.setLastName("Doe");
//        user.setPassword(passwordEncoder.encode("password"));
//        user.setEmail("tes@random.com");
//        user.setRoles(Arrays.asList(adminRole));
//        user.setEnabled (true);
//        userRepository.save(user);
//        alreadySetUp = true;
//
//
//    }
//    @Transactional
//    public Privilege createPrivilegeIfNotFound(String name) {
//        Privilege privilege = privilegeReposytory.findByName(name);
//        if (privilege == null) {
//            privilege = new Privilege(name);
//            privilegeReposytory.save(privilege);
//        }
//        return privilege;
//    }
//    @Transactional
//    public Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
//        Role role = roleRepository.findByName(name);
//        if (role == null) {
//            role = new Role(name);
//            role.setPrivileges(privileges);
//            roleRepository.save(role);
//        }
//        return role;
//    }
//}
