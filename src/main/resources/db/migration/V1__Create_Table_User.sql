CREATE TYPE user_roles as ENUM ('SuperUser', 'Admin', 'User');

CREATE TABLE users(
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(20) NOT NULL,
    role user_roles NOT NULL
)

