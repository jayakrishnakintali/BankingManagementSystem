package com.banking.model;

import java.sql.Timestamp;

/** Customer personal details (table: customers). */
public class Customer {
    private long customerId;
    private String fullName, email, phone, address;
    private Timestamp createdAt;

    public Customer() { }
    public Customer(String fullName, String email, String phone, String address) {
        this.fullName = fullName; this.email = email; this.phone = phone; this.address = address;
    }
    public long getCustomerId() { return customerId; }
    public void setCustomerId(long v) { this.customerId = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
}
