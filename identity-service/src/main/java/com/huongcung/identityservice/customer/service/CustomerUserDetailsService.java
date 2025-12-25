package com.huongcung.identityservice.customer.service;

import com.huongcung.identityservice.common.dto.CustomUserDetails;
import com.huongcung.identityservice.customer.entity.CustomerEntity;
import com.huongcung.identityservice.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("customerUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading customer by email: {}", username);

        Optional<CustomerEntity> customerEntityOptional = customerRepository.findByUsername(username);
        if (customerEntityOptional.isEmpty()) {
            customerEntityOptional = customerRepository.findByEmail(username);
        }
        CustomerEntity customer = customerEntityOptional
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with username: " + username));

        log.info("Customer found: {} with ID: {}", customer.getUsername(), customer.getId());

        return CustomUserDetails.create(customer);
    }
}
