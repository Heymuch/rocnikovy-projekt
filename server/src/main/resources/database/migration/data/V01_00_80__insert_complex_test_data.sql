-- 1. VLAKOVÉ SOUPRAVY (3 vlaky: 1 doručený, 1 v přepravě, 1 potvrzený budoucí)
-- Praha hl.n.:     71c9cdbf-ed69-5dbe-bc45-4f678ca1242c
-- Pardubice hl.n.: 69fc8ac3-3141-5781-943c-7f9415eae880
-- Brno hl.n.:      d4783576-b7ae-5d6b-8cb5-19c5dc8f837e
-- Ostrava hl.n.:   82cf53b9-05f5-5e78-b6d2-657c1931558f
-- Plzeň hl.n.:     b10d209e-664a-5099-83dd-51fc78566e34
insert into "train_sets" ("id", "locomotive_id", "station_from", "station_to", "code",
                          "departure_date", "arrival_date", "expiration_date")
values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 4,
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        'TRAIN-2026-001', '2026-11-24 08:00:00+01', '2026-11-24 14:30:00+01', '2026-11-24 18:00:00+01'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 5,
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        'TRAIN-2026-002', '2026-12-09 07:00:00+01', '2026-12-09 13:30:00+01', '2026-12-09 17:00:00+01'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 3,
        'b10d209e-664a-5099-83dd-51fc78566e34', 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e',
        'TRAIN-2026-003', '2026-12-13 09:00:00+01', '2026-12-13 15:00:00+01', '2026-12-13 18:00:00+01')
on conflict ("id") do nothing;

-- 2. OBJEDNÁVKY (7 kusů – Harry Potter & Ron Weasley, všechny stavy a typy nákladů)
insert into "orders" ("id", "user_id", "train_set_id", "name", "status", "code",
                      "total_price", "locomotive_price", "load_date", "unload_date",
                      "preferred_departure", "preferred_arrival")
values
    -- Harry: DELIVERED (Vlak 1: Praha -> Ostrava, jel sám)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'a1b05d0e-0000-4000-8000-000000000002',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Dřevo Praha – Ostrava', 'DELIVERED', 'ORD-2026-001',
     62450.00, 21500.00, '2026-11-23 14:00:00+01', '2026-11-24 16:00:00+01',
     '2026-11-24 07:00:00+01', '2026-11-24 18:00:00+01'),

    -- Harry: IN_TRANSIT (Vlak 2: Praha -> Brno, sdílí Vectron 24 000 Kč s Ronem)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'a1b05d0e-0000-4000-8000-000000000002',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'Černé uhlí Praha – Brno', 'IN_TRANSIT', 'ORD-2026-002',
     34800.00, 14400.00, '2026-12-08 12:00:00+01', '2026-12-09 12:00:00+01',
     '2026-12-09 06:00:00+01', '2026-12-09 14:00:00+01'),

    -- Ron: IN_TRANSIT (Vlak 2: Brno -> Ostrava, sdílí Vectron 24 000 Kč s Harrym)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc3', 'a1b05d0e-0000-4000-8000-000000000003',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'Stavební písek Brno – Ostrava', 'IN_TRANSIT', 'ORD-2026-003',
     26900.00, 9600.00, '2026-12-09 09:00:00+01', '2026-12-09 15:30:00+01',
     '2026-12-09 09:30:00+01', '2026-12-09 16:00:00+01'),

    -- Ron: CONFIRMED (Vlak 3: Plzeň -> Brno, TRAXX 19 500 Kč)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc4', 'a1b05d0e-0000-4000-8000-000000000003',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'Železná ruda Plzeň – Brno', 'CONFIRMED', 'ORD-2026-004',
     51200.00, 19500.00, '2026-12-12 10:00:00+01', '2026-12-13 17:00:00+01',
     '2026-12-13 08:00:00+01', '2026-12-13 18:00:00+01'),

    -- Harry: IN_QUEUE (čeká ve frontě, započítána max lokomotiva 24 000 Kč)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc5', 'a1b05d0e-0000-4000-8000-000000000002',
     null, 'Tekuté chemikálie Pardubice – Ostrava', 'IN_QUEUE', 'ORD-2026-005',
     49600.00, 24000.00, null, null,
     '2026-12-19 08:00:00+01', '2026-12-19 18:00:00+01'),

    -- Harry: IN_QUEUE (krátká trasa pro ukázku fixní ceny)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc6', 'a1b05d0e-0000-4000-8000-000000000002',
     null, 'Kulatinové dřevo Plzeň – Praha', 'IN_QUEUE', 'ORD-2026-006',
     33200.00, 24000.00, null, null,
     '2026-12-20 07:00:00+01', '2026-12-20 14:00:00+01'),

    -- Ron: IN_QUEUE (čeká ve frontě, lze testovat editaci i smazání za Rona)
    ('cccccccc-cccc-cccc-cccc-ccccccccccc7', 'a1b05d0e-0000-4000-8000-000000000003',
     null, 'Štěrk Pardubice – Brno', 'IN_QUEUE', 'ORD-2026-007',
     41850.00, 24000.00, null, null,
     '2026-12-21 09:00:00+01', '2026-12-21 17:00:00+01')
on conflict ("id") do nothing;

-- 3. TRASY OBJEDNÁVEK (spočítané přes pgRouting)
insert into "order_routes" ("id", "order_id", "station_from", "station_to", "route")
values ('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1',
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        array(select "node_id" from "find_shortest_path"('71c9cdbf-ed69-5dbe-bc45-4f678ca1242c'::uuid, '82cf53b9-05f5-5e78-b6d2-657c1931558f'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2',
        '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c', 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e',
        array(select "node_id" from "find_shortest_path"('71c9cdbf-ed69-5dbe-bc45-4f678ca1242c'::uuid, 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3',
        'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        array(select "node_id" from "find_shortest_path"('d4783576-b7ae-5d6b-8cb5-19c5dc8f837e'::uuid, '82cf53b9-05f5-5e78-b6d2-657c1931558f'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'cccccccc-cccc-cccc-cccc-ccccccccccc4',
        'b10d209e-664a-5099-83dd-51fc78566e34', 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e',
        array(select "node_id" from "find_shortest_path"('b10d209e-664a-5099-83dd-51fc78566e34'::uuid, 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'cccccccc-cccc-cccc-cccc-ccccccccccc5',
        '69fc8ac3-3141-5781-943c-7f9415eae880', '82cf53b9-05f5-5e78-b6d2-657c1931558f',
        array(select "node_id" from "find_shortest_path"('69fc8ac3-3141-5781-943c-7f9415eae880'::uuid, '82cf53b9-05f5-5e78-b6d2-657c1931558f'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd6', 'cccccccc-cccc-cccc-cccc-ccccccccccc6',
        'b10d209e-664a-5099-83dd-51fc78566e34', '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c',
        array(select "node_id" from "find_shortest_path"('b10d209e-664a-5099-83dd-51fc78566e34'::uuid, '71c9cdbf-ed69-5dbe-bc45-4f678ca1242c'::uuid) order by "seq")),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd7', 'cccccccc-cccc-cccc-cccc-ccccccccccc7',
        '69fc8ac3-3141-5781-943c-7f9415eae880', 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e',
        array(select "node_id" from "find_shortest_path"('69fc8ac3-3141-5781-943c-7f9415eae880'::uuid, 'd4783576-b7ae-5d6b-8cb5-19c5dc8f837e'::uuid) order by "seq"))
on conflict ("id") do nothing;

-- 4. NÁKLADY OBJEDNÁVEK
insert into "order_cargoes" ("id", "order_id", "cargo_type_id", "volume", "weight", "wagon_id", "wagon_count")
values ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 1, 210.0, 120.0, 1, 3), -- WOOD -> Roos
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 2, 130.0,  95.0, 2, 2), -- COAL -> Eaos
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 4, 120.0, 110.0, 4, 2), -- BULK_MATERIALS -> Tadns
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'cccccccc-cccc-cccc-cccc-ccccccccccc4', 3,  90.0, 125.0, 3, 2), -- IRON_ORE -> Falns
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', 'cccccccc-cccc-cccc-cccc-ccccccccccc5', 5, 160.0, 110.0, 5, 2), -- LIQUID_CHEMICALS -> Zacns
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee6', 'cccccccc-cccc-cccc-cccc-ccccccccccc6', 1,  70.0,  45.0, 1, 1), -- WOOD -> Roos
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee7', 'cccccccc-cccc-cccc-cccc-ccccccccccc7', 4, 180.0, 150.0, 4, 3)  -- BULK_MATERIALS -> Tadns
on conflict ("id") do nothing;

-- 5. HISTORIE STAVŮ OBJEDNÁVEK
insert into "order_status_history" ("id", "order_id", "status", "changed_at")
values ('ffffffff-ffff-ffff-ffff-fffffffffff1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'IN_QUEUE',   '2026-11-16 09:00:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff2', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'CONFIRMED',  '2026-11-18 14:20:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'IN_TRANSIT', '2026-11-24 08:05:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff4', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'DELIVERED',  '2026-11-24 14:35:00+01'),

       ('ffffffff-ffff-ffff-ffff-fffffffffff5', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 'IN_QUEUE',   '2026-11-25 10:15:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff6', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 'CONFIRMED',  '2026-11-26 16:00:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff7', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', 'IN_TRANSIT', '2026-12-09 07:05:00+01'),

       ('ffffffff-ffff-ffff-ffff-fffffffffff8', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 'IN_QUEUE',   '2026-11-25 11:30:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff9', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 'CONFIRMED',  '2026-11-26 16:00:00+01'),
       ('ffffffff-ffff-ffff-ffff-ffffffffff10', 'cccccccc-cccc-cccc-cccc-ccccccccccc3', 'IN_TRANSIT', '2026-12-09 09:10:00+01'),

       ('ffffffff-ffff-ffff-ffff-ffffffffff11', 'cccccccc-cccc-cccc-cccc-ccccccccccc4', 'IN_QUEUE',   '2026-11-26 08:45:00+01'),
       ('ffffffff-ffff-ffff-ffff-ffffffffff12', 'cccccccc-cccc-cccc-cccc-ccccccccccc4', 'CONFIRMED',  '2026-11-27 13:10:00+01'),

       ('ffffffff-ffff-ffff-ffff-ffffffffff13', 'cccccccc-cccc-cccc-cccc-ccccccccccc5', 'IN_QUEUE',   '2026-11-27 15:00:00+01'),
       ('ffffffff-ffff-ffff-ffff-ffffffffff14', 'cccccccc-cccc-cccc-cccc-ccccccccccc6', 'IN_QUEUE',   '2026-11-28 08:30:00+01'),
       ('ffffffff-ffff-ffff-ffff-ffffffffff15', 'cccccccc-cccc-cccc-cccc-ccccccccccc7', 'IN_QUEUE',   '2026-11-28 09:15:00+01')
on conflict ("id") do nothing;
