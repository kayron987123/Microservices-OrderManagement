package com.gad.msvc_customer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Long id;

    @Column(name = "uuid_permission", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String name;
}
