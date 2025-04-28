package com.gad.msvc_customer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.generator.EventType;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_customer")
    private Long id;

    @Column(name = "uuid_customer", nullable = false, unique = true, updatable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
    @org.hibernate.annotations.Generated(event = EventType.INSERT)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @ManyToMany
    @JoinTable(
            name = "customers_roles",
            joinColumns = @JoinColumn(name = "id_customer"),
            inverseJoinColumns = @JoinColumn(name = "id_role"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_customer", "id_role"})
    )
    private Set<Role> roles;
}
