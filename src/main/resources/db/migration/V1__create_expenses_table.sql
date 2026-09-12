CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    category VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    expense_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_expenses_expense_date ON expenses (expense_date);
CREATE INDEX idx_expenses_category ON expenses (category);
CREATE INDEX idx_expenses_description ON expenses (description);
