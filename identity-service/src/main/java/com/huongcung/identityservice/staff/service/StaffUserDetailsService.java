package com.huongcung.identityservice.staff.service;

import com.huongcung.identityservice.common.dto.CustomUserDetails;
import com.huongcung.identityservice.staff.entity.StaffEntity;
import com.huongcung.identityservice.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("staffUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class StaffUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading staff by username: {}", username);

        Optional<StaffEntity> staffEntityOptional = staffRepository.findByUsername(username);
        if (staffEntityOptional.isEmpty()) {
            staffEntityOptional = staffRepository.findByEmail(username);
        }
        StaffEntity staff = staffEntityOptional
                .orElseThrow(() -> new UsernameNotFoundException("Staff not found with username: " + username));

        log.info("Staff found: {} with ID: {}, type: {}", username, staff.getId(), staff.getStaffType());

        return CustomUserDetails.create(staff);
    }

}
