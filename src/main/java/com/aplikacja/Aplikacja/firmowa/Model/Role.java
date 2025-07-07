package com.aplikacja.Aplikacja.firmowa.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name= "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERoles name;

    // relacja z użytkownikami
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    // relacja z Privilege
    @ManyToMany
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    )
    private Collection<Privilege> privileges;

    // Konstruktor z nazwą roli
    public Role(ERoles name) {
        this.name = name;
    }
}