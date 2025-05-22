package com.paypal.integration.security.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permissions")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    private String description;

//    @ManyToMany(mappedBy = "permissions")
//    private Set<Role> roles = new HashSet<>();
}

