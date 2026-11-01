-- =====================================================================
-- Banking Management System : MySQL 8 script
-- Safe to re-run: tables use IF NOT EXISTS, sample data uses INSERT IGNORE
-- (existing data is never deleted). Procedures/trigger are re-created.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS banking_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE banking_system;

-- 1. branches ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS branches (
  branch_id   INT AUTO_INCREMENT PRIMARY KEY,
  branch_name VARCHAR(100) NOT NULL,
  branch_code VARCHAR(10)  NOT NULL UNIQUE,
  address     VARCHAR(255) NOT NULL,
  city        VARCHAR(60)  NOT NULL,
  ifsc_code   VARCHAR(11)  NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. customers ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
  customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name   VARCHAR(100) NOT NULL,
  email       VARCHAR(120) NOT NULL UNIQUE,
  phone       VARCHAR(15)  NOT NULL,
  address     VARCHAR(255) NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. users (login + role). customer_id is NULL for an administrator ----
CREATE TABLE IF NOT EXISTS users (
  user_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id   BIGINT NULL UNIQUE,
  username      VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  is_active     TINYINT(1) NOT NULL DEFAULT 1,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_users_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
  CONSTRAINT chk_users_customer CHECK (role = 'ADMIN' OR customer_id IS NOT NULL)
) ENGINE=InnoDB;

-- 4. accounts ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS accounts (
  account_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id    BIGINT NOT NULL,
  branch_id      INT NOT NULL,
  account_number VARCHAR(12) NOT NULL UNIQUE,
  account_type   ENUM('SAVINGS','CURRENT') NOT NULL DEFAULT 'SAVINGS',
  balance        DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  status         ENUM('ACTIVE','FROZEN','CLOSED') NOT NULL DEFAULT 'ACTIVE',
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_balance CHECK (balance >= 0),
  CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
  CONSTRAINT fk_accounts_branch   FOREIGN KEY (branch_id)   REFERENCES branches(branch_id),
  INDEX idx_accounts_customer (customer_id)
) ENGINE=InnoDB;

-- 5. transactions (a transfer = one TRANSFER_DEBIT + one TRANSFER_CREDIT row)
CREATE TABLE IF NOT EXISTS transactions (
  transaction_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id        BIGINT NOT NULL,
  transaction_type  ENUM('DEPOSIT','WITHDRAWAL','TRANSFER_DEBIT','TRANSFER_CREDIT') NOT NULL,
  amount            DECIMAL(15,2) NOT NULL,
  balance_after     DECIMAL(15,2) NOT NULL,
  reference_account VARCHAR(12) NULL,
  description       VARCHAR(255) NULL,
  transaction_date  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status            ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
  CONSTRAINT chk_tx_amount CHECK (amount > 0),
  CONSTRAINT fk_tx_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
  INDEX idx_tx_account_date (account_id, transaction_date)
) ENGINE=InnoDB;

-- 6. audit_logs (filled automatically by the trigger below) -----------
CREATE TABLE IF NOT EXISTS audit_logs (
  audit_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id  BIGINT NOT NULL,
  action_type VARCHAR(30) NOT NULL,
  amount      DECIMAL(15,2) NOT NULL,
  old_balance DECIMAL(15,2) NOT NULL,
  new_balance DECIMAL(15,2) NOT NULL,
  description VARCHAR(255) NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_audit_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
  INDEX idx_audit_account (account_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Sample data (passwords are PBKDF2 hashes: iterations:salt:hash)
--   admin               / Admin@123
--   aarav@example.com   / Aarav@123
--   diya@example.com    / Diya@123
-- ---------------------------------------------------------------------
INSERT IGNORE INTO branches (branch_id, branch_name, branch_code, address, city, ifsc_code)
VALUES (1, 'Main Branch', 'MB001', '1 College Road', 'Ludhiana', 'SBNK0000001');

INSERT IGNORE INTO customers (full_name, email, phone, address) VALUES
 ('Aarav Sharma', 'aarav@example.com', '9876543210', '12 Model Town, Ludhiana'),
 ('Diya Verma',   'diya@example.com',   '9876500001', '45 Sarabha Nagar, Ludhiana');

INSERT IGNORE INTO users (customer_id, username, password_hash, role)
SELECT NULL, 'admin', '65536:IwzWlxU8XtHTzwCE6ezFww==:j8tRLVU517OToqHcLUXgpuhNrjNfIjolRdrEKp+Lf5Y=', 'ADMIN';
INSERT IGNORE INTO users (customer_id, username, password_hash, role)
SELECT customer_id, 'aarav@example.com', '65536:DM4+ifCoSEwWSoW75w3z3Q==:znqqH3q7MEBCeQs0iXelWsVlIVKdFWTVt2Bzkqxl62M=', 'CUSTOMER' FROM customers WHERE email='aarav@example.com';
INSERT IGNORE INTO users (customer_id, username, password_hash, role)
SELECT customer_id, 'diya@example.com', '65536:P1g+WRB+LTNsQtwsS6JY0g==:txaR4eTbs1HHB3jjaApYgWHZvGmi/HLCTIxq3NuNEpk=', 'CUSTOMER' FROM customers WHERE email='diya@example.com';

INSERT IGNORE INTO accounts (customer_id, branch_id, account_number, account_type, balance)
SELECT customer_id, 1, '100000000001', 'SAVINGS', 10000.00 FROM customers WHERE email='aarav@example.com';
INSERT IGNORE INTO accounts (customer_id, branch_id, account_number, account_type, balance)
SELECT customer_id, 1, '100000000002', 'SAVINGS', 5000.00 FROM customers WHERE email='diya@example.com';

-- ---------------------------------------------------------------------
-- Trigger: audit every balance change automatically
-- ---------------------------------------------------------------------
DELIMITER $$

DROP TRIGGER IF EXISTS trg_accounts_audit$$
CREATE TRIGGER trg_accounts_audit
AFTER UPDATE ON accounts
FOR EACH ROW
BEGIN
  IF OLD.balance <> NEW.balance THEN
    INSERT INTO audit_logs (account_id, action_type, amount, old_balance, new_balance, description)
    VALUES (NEW.account_id,
            IF(NEW.balance > OLD.balance, 'CREDIT', 'DEBIT'),
            ABS(NEW.balance - OLD.balance),
            OLD.balance, NEW.balance,
            CONCAT('Balance changed from ', OLD.balance, ' to ', NEW.balance));
  END IF;
END$$

-- ---------------------------------------------------------------------
-- Stored procedure: deposit  (p_status = 'SUCCESS' or 'ERROR: <reason>')
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_deposit$$
CREATE PROCEDURE sp_deposit(IN p_account_id BIGINT, IN p_amount DECIMAL(15,2), OUT p_status VARCHAR(100))
proc: BEGIN
  DECLARE v_balance DECIMAL(15,2);
  DECLARE v_acc_status VARCHAR(10);
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    SET p_status = 'ERROR: Deposit failed due to a database error';
  END;

  IF p_amount IS NULL OR p_amount <= 0 THEN
    SET p_status = 'ERROR: Amount must be greater than zero';
    LEAVE proc;
  END IF;

  START TRANSACTION;
  SELECT balance, status INTO v_balance, v_acc_status
    FROM accounts WHERE account_id = p_account_id FOR UPDATE;   -- row lock

  IF v_acc_status IS NULL THEN
    ROLLBACK; SET p_status = 'ERROR: Account not found'; LEAVE proc;
  ELSEIF v_acc_status <> 'ACTIVE' THEN
    ROLLBACK; SET p_status = 'ERROR: Account is not active'; LEAVE proc;
  END IF;

  UPDATE accounts SET balance = balance + p_amount WHERE account_id = p_account_id;
  INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description)
  VALUES (p_account_id, 'DEPOSIT', p_amount, v_balance + p_amount, 'Cash deposit');
  COMMIT;
  SET p_status = 'SUCCESS';
END$$

-- ---------------------------------------------------------------------
-- Stored procedure: withdraw
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_withdraw$$
CREATE PROCEDURE sp_withdraw(IN p_account_id BIGINT, IN p_amount DECIMAL(15,2), OUT p_status VARCHAR(100))
proc: BEGIN
  DECLARE v_balance DECIMAL(15,2);
  DECLARE v_acc_status VARCHAR(10);
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    SET p_status = 'ERROR: Withdrawal failed due to a database error';
  END;

  IF p_amount IS NULL OR p_amount <= 0 THEN
    SET p_status = 'ERROR: Amount must be greater than zero';
    LEAVE proc;
  END IF;

  START TRANSACTION;
  SELECT balance, status INTO v_balance, v_acc_status
    FROM accounts WHERE account_id = p_account_id FOR UPDATE;

  IF v_acc_status IS NULL THEN
    ROLLBACK; SET p_status = 'ERROR: Account not found'; LEAVE proc;
  ELSEIF v_acc_status <> 'ACTIVE' THEN
    ROLLBACK; SET p_status = 'ERROR: Account is not active'; LEAVE proc;
  ELSEIF v_balance < p_amount THEN
    ROLLBACK; SET p_status = 'ERROR: Insufficient balance'; LEAVE proc;
  END IF;

  UPDATE accounts SET balance = balance - p_amount WHERE account_id = p_account_id;
  INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description)
  VALUES (p_account_id, 'WITHDRAWAL', p_amount, v_balance - p_amount, 'Cash withdrawal');
  COMMIT;
  SET p_status = 'SUCCESS';
END$$

-- ---------------------------------------------------------------------
-- Stored procedure: fund transfer (ONE database transaction).
-- Rows are locked in account_id order to avoid deadlocks.
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_transfer$$
CREATE PROCEDURE sp_transfer(IN p_from_id BIGINT, IN p_to_number VARCHAR(12),
                             IN p_amount DECIMAL(15,2), OUT p_status VARCHAR(100))
proc: BEGIN
  DECLARE v_to_id BIGINT;
  DECLARE v_from_bal, v_to_bal DECIMAL(15,2);
  DECLARE v_from_no VARCHAR(12);
  DECLARE v_from_st, v_to_st VARCHAR(10);
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    SET p_status = 'ERROR: Transfer failed and was rolled back';
  END;

  IF p_amount IS NULL OR p_amount <= 0 THEN
    SET p_status = 'ERROR: Amount must be greater than zero';
    LEAVE proc;
  END IF;

  SELECT account_id INTO v_to_id FROM accounts WHERE account_number = p_to_number;
  IF v_to_id IS NULL THEN
    SET p_status = 'ERROR: Receiver account not found';
    LEAVE proc;
  END IF;
  IF v_to_id = p_from_id THEN
    SET p_status = 'ERROR: Cannot transfer to your own account';
    LEAVE proc;
  END IF;

  START TRANSACTION;
  IF p_from_id < v_to_id THEN
    SELECT balance, status, account_number INTO v_from_bal, v_from_st, v_from_no
      FROM accounts WHERE account_id = p_from_id FOR UPDATE;
    SELECT balance, status INTO v_to_bal, v_to_st
      FROM accounts WHERE account_id = v_to_id FOR UPDATE;
  ELSE
    SELECT balance, status INTO v_to_bal, v_to_st
      FROM accounts WHERE account_id = v_to_id FOR UPDATE;
    SELECT balance, status, account_number INTO v_from_bal, v_from_st, v_from_no
      FROM accounts WHERE account_id = p_from_id FOR UPDATE;
  END IF;

  IF v_from_st IS NULL OR v_from_st <> 'ACTIVE' THEN
    ROLLBACK; SET p_status = 'ERROR: Your account is not active'; LEAVE proc;
  ELSEIF v_to_st <> 'ACTIVE' THEN
    ROLLBACK; SET p_status = 'ERROR: Receiver account is not active'; LEAVE proc;
  ELSEIF v_from_bal < p_amount THEN
    ROLLBACK; SET p_status = 'ERROR: Insufficient balance'; LEAVE proc;
  END IF;

  UPDATE accounts SET balance = balance - p_amount WHERE account_id = p_from_id;
  UPDATE accounts SET balance = balance + p_amount WHERE account_id = v_to_id;

  INSERT INTO transactions (account_id, transaction_type, amount, balance_after, reference_account, description)
  VALUES (p_from_id, 'TRANSFER_DEBIT', p_amount, v_from_bal - p_amount, p_to_number,
          CONCAT('Transfer to ', p_to_number));
  INSERT INTO transactions (account_id, transaction_type, amount, balance_after, reference_account, description)
  VALUES (v_to_id, 'TRANSFER_CREDIT', p_amount, v_to_bal + p_amount, v_from_no,
          CONCAT('Transfer from ', v_from_no));

  COMMIT;
  SET p_status = 'SUCCESS';
END$$

DELIMITER ;
