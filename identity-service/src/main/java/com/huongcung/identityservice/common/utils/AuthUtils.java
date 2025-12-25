package com.huongcung.identityservice.common.utils;

import com.huongcung.identityservice.customer.entity.CustomerEntity;
import com.huongcung.identityservice.staff.entity.StaffEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class AuthUtils {

    public static List<? extends GrantedAuthority> getAuthorities(StaffEntity staff) {
        return staff.getStaffType().getRoles()
                .parallelStream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    public static List<? extends GrantedAuthority> getAuthorities(CustomerEntity customer) {
        return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }
}
