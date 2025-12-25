package com.huongcung.identityservice.staff.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum StaffType {

    ADMIN(List.of(StaffRole.ROLE_ADMIN)),
    STORE_MANAGER(List.of(StaffRole.ROLE_STORE_MANAGER)),
    WAREHOUSE_MANAGER(List.of(StaffRole.ROLE_WAREHOUSE_MANAGER)),
    WAREHOUSE_STAFF(List.of(StaffRole.ROLE_WAREHOUSE_STAFF)),
    SUPPORT_AGENT(List.of(StaffRole.ROLE_SUPPORT_AGENT));

    private final List<StaffRole> roles;
}
