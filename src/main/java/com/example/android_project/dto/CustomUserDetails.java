package com.example.android_project.dto;

import com.example.android_project.entity.User;
import com.example.android_project.entity.UserProfile;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {

        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.emptyList();
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }
    @Override
    public String getUsername() {

        return null;
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

    public String getProviderId() {

        return user.getProviderId();
    }

    public UserProfile getUserProfile() {

        return user.getUserProfile();
    }
}