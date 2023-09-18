package com.example.models;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Email(message = "Wrong email format!")
    private String email;

    @NotNull
    private String password;

    @ElementCollection
    private Set<GrantedAuthority> authorities = new HashSet<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void addRole(GrantedAuthority grantedAuthority) {
        this.authorities.add(grantedAuthority);
    }
}