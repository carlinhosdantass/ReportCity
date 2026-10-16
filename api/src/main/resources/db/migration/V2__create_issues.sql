CREATE TABLE issues (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    neighborhood VARCHAR(120) NOT NULL,
    category_id INTEGER NOT NULL,
    CONSTRAINT fk_issues_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_issues_category_id ON issues(category_id);
