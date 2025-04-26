package com.varshneys.ecommerce.ecommerce_backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.varshneys.ecommerce.ecommerce_backend.Model.User;

public class UserDetailImpl implements UserDetails {
    private String email;
    private String password;
    private Long userId;
    private String name;
    private String role;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailImpl(String email, String password, Long userId, String name, String role,  Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.name = name;
        this.role = role; // Convert Role to String
        this.authorities = authorities;
    }

    public static UserDetailImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRole() != null
        ? List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        : List.of();
        return new UserDetailImpl(user.getEmail(), user.getPassword(), user.getUserId(), user.getName(), user.getRole().name(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    public String getRole() {
        return role;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
