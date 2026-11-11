-- 1. UŽIVATELÉ (Dva různí klienti)
INSERT INTO "users" (id, email, name, is_blocked) VALUES
                                                      ('11111111-1111-1111-1111-111111111111', 'firma.a@test.cz', 'Firma A s.r.o.', false),
                                                      ('22222222-2222-2222-2222-222222222222', 'firma.b@test.cz', 'Firma B a.s.', false)
ON CONFLICT (id) DO NOTHING;

-- 2. UZLY A STANICE (Praha, Brno, Ostrava, Plzeň)
INSERT INTO "railway_nodes" (id, latitude, longitude) VALUES
                                                          ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 50.088, 14.400), -- Praha
                                                          ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 49.195, 16.606), -- Brno
                                                          ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 49.820, 18.262), -- Ostrava
                                                          ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 49.738, 13.373)  -- Plzeň
ON CONFLICT (id) DO NOTHING;

INSERT INTO "stations" (id, name) VALUES
                                      ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Praha hl.n.'),
                                      ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'Brno hl.n.'),
                                      ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Ostrava hl.n.'),
                                      ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'Plzeň hl.n.')
ON CONFLICT (id) DO NOTHING;

-- 3. ČÍSELNÍKY (Lokomotivy a Vagóny)
INSERT INTO "locomotives" (id, name, max_towing_weight, max_speed) VALUES
                                                                       (1001, 'Škoda 109E (Vlak 1)', 2500, 200),
                                                                       (1002, 'Siemens Vectron (Vlak 2)', 3000, 160)
ON CONFLICT (id) DO NOTHING;

INSERT INTO "wagons" (id, name, tare_weight, net_capacity_weight, gross_weight_limit, volume_capacity, rent_price) VALUES
                                                                                                                       (1001, 'Uhlák (Eaos)', 22.0, 68.0, 90.0, 71.0, 15.0),
                                                                                                                       (1002, 'Plošinový (Res)', 24.0, 56.0, 80.0, 0.0, 12.0)
ON CONFLICT (id) DO NOTHING;

-- Pro jistotu doplníme testovací typ nákladu, pokud by chyběl z minula
INSERT INTO "cargo_types" (id, name) VALUES
    (999, 'TEST_CARGO_MIX')
ON CONFLICT (id) DO NOTHING;

-- 4. VLAKOVÉ SOUPRAVY
-- Vlak 1: Praha -> Ostrava (Poveze 3 objednávky)
-- Vlak 2: Plzeň -> Praha (Poveze 1 objednávku)
INSERT INTO "train_sets" (id, locomotive_id, station_from, station_to, code, departure_date, arrival_date, expiration_date) VALUES
                                                                                                                                ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 1001, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'TRAIN-MAIN-001', '2026-11-20 08:00:00+01', '2026-11-20 14:00:00+01', '2026-11-20 16:00:00+01'),
                                                                                                                                ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 1002, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'TRAIN-LOCAL-002', '2026-11-21 09:00:00+01', '2026-11-21 11:00:00+01', '2026-11-21 12:00:00+01')
ON CONFLICT (id) DO NOTHING;

-- 5. OBJEDNÁVKY (5 kusů)
INSERT INTO "orders" (id, user_id, train_set_id, name, status, code, total_price, load_date, unload_date, preferred_departure, preferred_arrival) VALUES
-- Objednávka 1 (Firma A) na Vlak 1 (Praha -> Brno)
('cccccccc-cccc-cccc-cccc-ccccccccccc1', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Přeprava uhlí PHA-BRN', 'IN_TRANSIT', 'ORD-001', 15000, '2026-11-19 08:00:00+01', '2026-11-20 18:00:00+01', '2026-11-20 07:00:00+01', '2026-11-20 12:00:00+01'),
-- Objednávka 2 (Firma B) na Vlak 1 (Brno -> Ostrava) - přistoupí po cestě
('cccccccc-cccc-cccc-cccc-ccccccccccc2', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Stavebniny BRN-OST', 'IN_TRANSIT', 'ORD-002', 12000, '2026-11-19 10:00:00+01', '2026-11-20 20:00:00+01', '2026-11-20 10:00:00+01', '2026-11-20 15:00:00+01'),
-- Objednávka 3 (Firma A) na Vlak 1 (Praha -> Ostrava) - jede celou trasu
('cccccccc-cccc-cccc-cccc-ccccccccccc3', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Dřevo PHA-OST', 'IN_TRANSIT', 'ORD-003', 25000, '2026-11-19 06:00:00+01', '2026-11-21 08:00:00+01', '2026-11-20 06:00:00+01', '2026-11-20 16:00:00+01'),
-- Objednávka 4 (Firma B) na Vlak 2 (Plzeň -> Praha)
('cccccccc-cccc-cccc-cccc-ccccccccccc4', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'Ocel PLZ-PHA', 'CONFIRMED', 'ORD-004', 18000, '2026-11-20 12:00:00+01', '2026-11-22 12:00:00+01', '2026-11-21 08:00:00+01', '2026-11-21 13:00:00+01'),
-- Objednávka 5 (Firma A) ČEKÁ VE FRONTĚ (Train set je NULL)
('cccccccc-cccc-cccc-cccc-ccccccccccc5', '11111111-1111-1111-1111-111111111111', NULL, 'Písek OST-PLZ', 'IN_QUEUE', 'ORD-005', 30000, '2026-11-25 08:00:00+01', '2026-11-27 18:00:00+01', '2026-11-26 08:00:00+01', '2026-11-26 18:00:00+01')
ON CONFLICT (id) DO NOTHING;

-- 6. TRASY OBJEDNÁVEK
INSERT INTO "order_routes" (id, order_id, station_from, station_to, route) VALUES
                                                                               ('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', ARRAY['aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'::uuid, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'::uuid]),
                                                                               ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', ARRAY['aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'::uuid, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'::uuid]),
                                                                               ('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', ARRAY['aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'::uuid, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'::uuid]),
                                                                               ('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'cccccccc-cccc-cccc-cccc-ccccccccccc4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', ARRAY['aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4'::uuid, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'::uuid]),
                                                                               ('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'cccccccc-cccc-cccc-cccc-ccccccccccc5', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', ARRAY['aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'::uuid, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4'::uuid])
ON CONFLICT (id) DO NOTHING;

-- 7. NÁKLADY OBJEDNÁVEK
INSERT INTO "order_cargoes" (id, order_id, cargo_type_id, volume, weight, wagon_id, wagon_count) VALUES
                                                                                                     ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 999, 100.0, 50.0, 1001, 5),
                                                                                                     ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 999, 80.0,  40.0, 1002, 3),
                                                                                                     ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 999, 200.0, 120.0, 1002, 10),
                                                                                                     ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'cccccccc-cccc-cccc-cccc-ccccccccccc4', 999, 50.0,  60.0, 1001, 4),
                                                                                                     ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', 'cccccccc-cccc-cccc-cccc-ccccccccccc5', 999, 300.0, 200.0, 1001, 15)
ON CONFLICT (id) DO NOTHING;
