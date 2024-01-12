package com.taskmanagement.security;

import com.taskmanagement.enums.UserRole;
import com.taskmanagement.model.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserRole role;

    public CustomUserDetails(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.name = userEntity.getName();
        this.surname = userEntity.getSurname();
        this.email = userEntity.getEmail();
        this.password = userEntity.getPassword();
        this.role = userEntity.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
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
