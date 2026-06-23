package com.studentmanagement.common.security;

import com.studentmanagement.auth.model.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private UUID id;
    private String email;
    private String password;
    private String role;
    private boolean enabled;

    public static UserPrincipal from(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .role(user.getRole())
                .enabled(user.getStatus().name().equals("ACTIVE"))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
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
        return enabled;
    }
}
