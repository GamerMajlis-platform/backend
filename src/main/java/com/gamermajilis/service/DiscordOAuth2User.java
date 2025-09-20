package com.gamermajilis.service;

import com.gamermajilis.model.User;
import com.gamermajilis.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscordOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public DiscordOAuth2User(Map<String, Object> attributes, User user) {
        this.attributes = attributes;
        this.user = user;
        this.authorities = mapRolesToAuthorities(user.getRoles());
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return user.getDiscordId();
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }
}