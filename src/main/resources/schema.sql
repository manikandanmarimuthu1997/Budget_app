-- Create the schema
CREATE SCHEMA budget_management;

-- Use the schema
USE budget_management;

-- Create department table
CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    current_budget DECIMAL(19, 2),
    yearly_allocation DECIMAL(19, 2)
);

-- Create budget_request table
CREATE TABLE budget_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requested_amount DECIMAL(19, 2),
    purpose VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    requested_by VARCHAR(255),
    approved_by VARCHAR(255),
    date_created TIMESTAMP,
    last_updated TIMESTAMP,
    department_id BIGINT,
    CONSTRAINT fk_budget_request_department FOREIGN KEY (department_id) REFERENCES department(id)
);

-- Create audit_log table
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255),
    entity_id BIGINT,
    entity_type VARCHAR(255),
    old_value TEXT,
    new_value TEXT,
    changed_by VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
