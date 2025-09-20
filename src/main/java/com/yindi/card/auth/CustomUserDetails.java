package com.yindi.card.auth;

import com.yindi.card.user.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private final User user;
    public CustomUserDetails(User user) { this.user = user; }
    public User getDomainUser() { return user; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 如果暂时没有角色，先返回空列表
        return List.of();
    }
    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
