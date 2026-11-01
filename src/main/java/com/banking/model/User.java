package com.banking.model;

/** A login record (table: users) joined with the customer's name. */
public class User {
    private final long userId;
    private final Long customerId;      // null for ADMIN
    private final String username;
    private final String passwordHash;
    private final String role;          // CUSTOMER or ADMIN
    private final boolean active;
    private final String fullName;

    public User(long userId, Long customerId, String username, String passwordHash,
                String role, boolean active, String fullName) {
        this.userId = userId; this.customerId = customerId; this.username = username;
        this.passwordHash = passwordHash; this.role = role; this.active = active; this.fullName = fullName;
    }
    public long getUserId() { return userId; }
    public Long getCustomerId() { return customerId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public String getFullName() { return fullName; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
