package com.mengzhidui.a7springboot.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface AppUser extends UserDetails {
    Long getId();
    String getType();
}
