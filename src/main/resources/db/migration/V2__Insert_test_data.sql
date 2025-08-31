-- Простая реализация генерации данных ввиде миграции
CREATE OR REPLACE FUNCTION generate_entry_data(record_count INTEGER)
    RETURNS VOID AS
$$
DECLARE
    batch_size CONSTANT INTEGER := 10000;
    batch_count         INTEGER;
BEGIN
    batch_count := CEIL(record_count::FLOAT / batch_size);

    FOR i IN 1..batch_count
        LOOP
            INSERT INTO entry (name, position)
            SELECT 'Entry ' || (batch_size * (i - 1) + num),
                   batch_size * (i - 1) + num
            FROM generate_series(1, LEAST(batch_size, record_count - batch_size * (i - 1))) AS num;

            IF i % 10 = 0 THEN
                COMMIT;
            END IF;
        END LOOP;

    COMMIT;
END;
$$ LANGUAGE plpgsql;

DO
$$
    BEGIN
        IF (SELECT COUNT(*) FROM entry) = 0 THEN
            RAISE NOTICE 'Generating 10M test records...';
            PERFORM generate_entry_data(10000000);
            RAISE NOTICE 'Test data generation completed';
        END IF;
    END
$$;