package com.aplikacja.Aplikacja.firmowa.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;
    private LocalDateTime signUpDate;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> role = new HashSet<>();

    @Builder
    public User(String firstName, String lastName, String login, String password, String email,
                Set<Role> role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.email = email;
        this.role = role;
        this.signUpDate = LocalDateTime.now();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return firstName.equals(user.firstName) && lastName.equals(user.lastName) &&
                email.equals(user.email);
    }

    public int hashCode() {
        return Objects.hash(firstName, lastName, email);
    }

}