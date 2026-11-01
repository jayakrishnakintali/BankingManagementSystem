package com.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** Bank account (table: accounts) plus branch name/IFSC for display. */
public class Account {
    private long accountId, customerId;
    private int branchId;
    private String accountNumber, accountType, status, branchName, ifscCode;
    private BigDecimal balance;
    private Timestamp createdAt;

    public long getAccountId() { return accountId; }
    public void setAccountId(long v) { this.accountId = v; }
    public long getCustomerId() { return customerId; }
    public void setCustomerId(long v) { this.customerId = v; }
    public int getBranchId() { return branchId; }
    public void setBranchId(int v) { this.branchId = v; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String v) { this.accountNumber = v; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String v) { this.accountType = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String v) { this.branchName = v; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String v) { this.ifscCode = v; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal v) { this.balance = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { this.createdAt = v; }
}
