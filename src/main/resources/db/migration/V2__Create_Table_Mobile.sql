CREATE TABLE mobiles (
    mobile_id SERIAL PRIMARY KEY,
    mobile_name VARCHAR(20),
    mobile_model VARCHAR(20),
    mobile_price NUMERIC,
    user_id SERIAL REFERENCES users(user_id)
)