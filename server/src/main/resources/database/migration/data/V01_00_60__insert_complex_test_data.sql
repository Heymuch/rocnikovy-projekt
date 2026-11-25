-- 1. UŽIVATELÉ (Dva různí klienti)
insert into "users" ("id", "email", "name", "is_blocked")
values ('11111111-1111-1111-1111-111111111111', 'firma.a@test.cz', 'Firma A s.r.o.', false),
       ('22222222-2222-2222-2222-222222222222', 'firma.b@test.cz', 'Firma B a.s.', false)
on conflict ("id") do nothing;

-- 2. LOKOMOTIVY
insert into "locomotives" ("id", "name", "max_towing_weight", "max_speed")
values (1001, 'Škoda 109E', 2500, 200),
       (1002, 'Siemens Vectron', 3000, 160)
on conflict ("id") do nothing;

-- 3. VLAKOVÉ SOUPRAVY
-- Vlak 1: Praha hl.n. -> Ostrava hl.n.
-- Vlak 2: Plzeň hl.n. -> Praha hl.n.
insert into "train_sets" ("id", "locomotive_id", "station_from", "station_to", "code", "departure_date", "arrival_date",
                          "expiration_date")
values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 1001,
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        'TRAIN-MAIN-001', '2026-11-20 08:00:00+01', '2026-11-20 14:00:00+01', '2026-11-20 16:00:00+01'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 1002,
        'b10d209e-664a-5099-83dd-51fc78566e34', '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c',
        'TRAIN-LOCAL-002', '2026-11-21 09:00:00+01', '2026-11-21 11:00:00+01', '2026-11-21 12:00:00+01')
on conflict ("id") do nothing;

-- 4. OBJEDNÁVKY (5 kusů)
insert into "orders" ("id", "user_id", "train_set_id", "name", "status", "code", "total_price", "load_date",
                      "unload_date", "preferred_departure", "preferred_arrival")
values ('cccccccc-cccc-cccc-cccc-ccccccccccc1', '11111111-1111-1111-1111-111111111111',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Přeprava uhlí PHA-BRN', 'IN_TRANSIT', 'ORD-001', 15000,
        '2026-11-19 08:00:00+01', '2026-11-20 18:00:00+01', '2026-11-20 07:00:00+01', '2026-11-20 12:00:00+01'),
       ('cccccccc-cccc-cccc-cccc-ccccccccccc2', '22222222-2222-2222-2222-222222222222',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Stavebniny BRN-OST', 'IN_TRANSIT', 'ORD-002', 12000,
        '2026-11-19 10:00:00+01', '2026-11-20 20:00:00+01', '2026-11-20 10:00:00+01', '2026-11-20 15:00:00+01'),
       ('cccccccc-cccc-cccc-cccc-ccccccccccc3', '11111111-1111-1111-1111-111111111111',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Dřevo PHA-OST', 'IN_TRANSIT', 'ORD-003', 25000,
        '2026-11-19 06:00:00+01', '2026-11-21 08:00:00+01', '2026-11-20 06:00:00+01', '2026-11-20 16:00:00+01'),
       ('cccccccc-cccc-cccc-cccc-ccccccccccc4', '22222222-2222-2222-2222-222222222222',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'Železná ruda PLZ-PHA', 'CONFIRMED', 'ORD-004', 18000,
        '2026-11-20 12:00:00+01', '2026-11-22 12:00:00+01', '2026-11-21 08:00:00+01', '2026-11-21 13:00:00+01'),
       ('cccccccc-cccc-cccc-cccc-ccccccccccc5', '11111111-1111-1111-1111-111111111111', null, 'Písek OST-PLZ',
        'IN_QUEUE', 'ORD-005', 30000, null, null, '2026-11-26 08:00:00+01', '2026-11-26 18:00:00+01')
on conflict ("id") do nothing;

-- 5. TRASY OBJEDNÁVEK (včetně spočítané reálné trasy přes find_shortest_path)
insert into "order_routes" ("id", "order_id", "station_from", "station_to", "route")
values ('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1',
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e',
        array(select "node_id" from "find_shortest_path"('71c9cdbf-ed69-5dbe-bc45-4f678ca1242c'::uuid, 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2',
        'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        array(select "node_id" from "find_shortest_path"('d4783576-b7ae-5d6b-8cb5-19c5dc8f837e'::uuid, '82cf53b9-05f5-5e78-b6d2-657c1931558f'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3',
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        array(select "node_id" from "find_shortest_path"('71c9cdbf-ed69-5dbe-bc45-4f678ca1242c'::uuid, '82cf53b9-05f5-5e78-b6d2-657c1931558f'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'cccccccc-cccc-cccc-cccc-ccccccccccc4',
        'b10d209e-664a-5099-83dd-51fc78566e34', '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c',
        array(select "node_id" from "find_shortest_path"('b10d209e-664a-5099-83dd-51fc78566e34'::uuid, '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'cccccccc-cccc-cccc-cccc-ccccccccccc5',
        '82cf53b9-05f5-5e78-b6d2-657c1931558f', 'b10d209e-664a-5099-83dd-51fc78566e34',
        array(select "node_id" from "find_shortest_path"('82cf53b9-05f5-5e78-b6d2-657c1931558f'::uuid, 'b10d209e-664a-5099-83dd-51fc78566e34'::uuid) order by "seq"))
on conflict ("id") do nothing;

-- 6. NÁKLADY OBJEDNÁVEK (napojeno na reálné cargo_types 1..5 a wagons 1..5)
insert into "order_cargoes" ("id", "order_id", "cargo_type_id", "volume", "weight", "wagon_id", "wagon_count")
values ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 2, 100.0, 50.0, 2, 2),  -- COAL -> Eaos
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 4, 80.0,  40.0, 4, 2),  -- BULK_MATERIALS -> Tadns
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 1, 200.0, 120.0, 1, 3), -- WOOD -> Roos
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'cccccccc-cccc-cccc-cccc-ccccccccccc4', 3, 50.0,  60.0, 3, 1),  -- IRON_ORE -> Falns
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', 'cccccccc-cccc-cccc-cccc-ccccccccccc5', 4, 300.0, 200.0, 4, 5)  -- BULK_MATERIALS -> Tadns
on conflict ("id") do nothing;
