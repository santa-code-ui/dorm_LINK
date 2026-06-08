-- Migration 001 : initial schema
-- Apply order: schema.sql  ->  seed_data.sql
-- Future incremental changes go in numbered files here, e.g.
--   002_add_visitor_log.sql
--   003_alter_room_add_ac_flag.sql
-- Keep each migration idempotent where possible.
SOURCE ../schema.sql;
