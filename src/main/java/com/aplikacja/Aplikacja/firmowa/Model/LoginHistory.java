package com.aplikacja.Aplikacja.firmowa.Model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    @ManyToOne
    private User user;
}
