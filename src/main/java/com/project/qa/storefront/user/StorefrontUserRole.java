package com.project.qa.storefront.user;

import com.project.qa.helpers.models.users.UserRole;

public enum StorefrontUserRole implements UserRole {
    SHOPPER,
    GUEST;

    @Override
    public String getRoleName() {
        return name();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
