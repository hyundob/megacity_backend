-- Apply only when REP_DATA_HG_SYS_STATE already exists. Preserves inventory and history.
BEGIN;
DO $$
DECLARE item RECORD;
BEGIN
  FOR item IN
    SELECT conname FROM pg_constraint
    WHERE conrelid = 'rep_data_hg_sys_state'::regclass AND contype = 'c'
      AND lower(pg_get_constraintdef(oid)) LIKE '%stored_bg_nm3%'
      AND lower(pg_get_constraintdef(oid)) LIKE '%refill_target%'
  LOOP
    EXECUTE format('ALTER TABLE rep_data_hg_sys_state DROP CONSTRAINT %I', item.conname);
  END LOOP;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = 'rep_data_hg_sys_state'::regclass AND conname = 'ck_hg_bg_capacity') THEN
    ALTER TABLE rep_data_hg_sys_state ADD CONSTRAINT ck_hg_bg_capacity CHECK (stored_bg_nm3 <= 20000);
  END IF;
END $$;
COMMIT;
