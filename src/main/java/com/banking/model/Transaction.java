package com.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** One row of the transactions table. */
public class Transaction {
    private long transactionId, accountId;
    private String transactionType, referenceAccount, description, status;
    private BigDecimal amount, balanceAfter;
    private Timestamp transactionDate;

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long v) { this.transactionId = v; }
    public long getAccountId() { return accountId; }
    public void setAccountId(long v) { this.accountId = v; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String v) { this.transactionType = v; }
    public String getReferenceAccount() { return referenceAccount; }
    public void setReferenceAccount(String v) { this.referenceAccount = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { this.amount = v; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal v) { this.balanceAfter = v; }
    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp v) { this.transactionDate = v; }
}
