package com.huongcung.identityservice.staff.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum representing user roles with Spring Security integration
 */
@RequiredArgsConstructor
@Getter
public enum StaffRole {

    ROLE_ADMIN,
    ROLE_STORE_MANAGER,
    ROLE_WAREHOUSE_MANAGER,
    ROLE_WAREHOUSE_STAFF,
    ROLE_SUPPORT_AGENT;
}
