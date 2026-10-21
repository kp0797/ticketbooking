ALTER TABLE show_seats
    ADD COLUMN hold_token UUID,
    ADD COLUMN hold_expires_at TIMESTAMP WITH TIME ZONE;