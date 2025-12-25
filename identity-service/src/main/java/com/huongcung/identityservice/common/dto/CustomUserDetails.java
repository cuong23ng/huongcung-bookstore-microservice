package com.huongcung.identityservice.common.dto;

import com.huongcung.identityservice.common.enumeration.UserType;
import com.huongcung.identityservice.common.utils.AuthUtils;
import com.huongcung.identityservice.customer.entity.CustomerEntity;
import com.huongcung.identityservice.staff.entity.StaffEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private Long id;
    private String email;
    private String username;
    private String password;
    private boolean emailVerified;
    private Collection<? extends GrantedAuthority> authorities;

    private UserType type;

    public static CustomUserDetails create(CustomerEntity customer) {
        List<? extends GrantedAuthority> authorities = AuthUtils.getAuthorities(customer);

        return new CustomUserDetails(
            customer.getId(),
            customer.getEmail(),
            customer.getUsername(),
            customer.getPasswordHash(),
            customer.getEmailVerified(),
            authorities,
            UserType.CUSTOMER
        );
    }

    public static CustomUserDetails create(StaffEntity staff) {
        List<? extends GrantedAuthority> authorities = AuthUtils.getAuthorities(staff);
        
        return new CustomUserDetails(
            staff.getId(),
            staff.getEmail(),
            staff.getUsername(),
            staff.getPasswordHash(),
            staff.getEmailVerified(),
            authorities,
            UserType.STAFF
        );
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
        return Optional.ofNullable(username).orElse(email);
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
}
