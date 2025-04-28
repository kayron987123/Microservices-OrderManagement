package com.gad.msvc_oauth.service;

import com.gad.msvc_oauth.model.Customer;
import com.gad.msvc_oauth.service.feign.CustomerServiceFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerServiceFeign customerServiceFeign;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerServiceFeign.findByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException("Customer with email " + email + " not found");
        }

        List<SimpleGrantedAuthority> rolesAuthorities = customer.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

        List<SimpleGrantedAuthority> permissionsAuthorities = customer.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(SimpleGrantedAuthority::new)
                .toList();

        rolesAuthorities.addAll(permissionsAuthorities);

        return new User(
                customer.getEmail(),
                customer.getPassword(),
                true,
                true,
                true,
                true,
                rolesAuthorities
        );
    }
}
