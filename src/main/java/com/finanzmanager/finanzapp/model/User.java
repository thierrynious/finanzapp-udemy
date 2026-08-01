package com.finanzmanager.finanzapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username darf nicht leer sein")
    @Size(min = 3, max = 50, message = "Username muss zwischen 3 und 50 Zeichen haben")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "E-mail darf nicht leer sein")
    @Email(message = "Bitte eine gültige E-Mail-Adresse angeben")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Passwort darf nicht leer sein")
    @Size(min=8,message = "Passwort muss mindestens 8 Zeichen haben")
    @Column(nullable = false, length = 255)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
