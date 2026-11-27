insert into "order_status_history" ("id", "order_id", "status", "changed_at")
values ('ffffffff-ffff-ffff-ffff-fffffffffff1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'IN_QUEUE',   '2026-11-18 10:00:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff2', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'CONFIRMED',  '2026-11-18 14:30:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff3', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', 'IN_TRANSIT', '2026-11-20 08:05:00+01'),
       ('ffffffff-ffff-ffff-ffff-fffffffffff4', 'cccccccc-cccc-cccc-cccc-ccccccccccc5', 'IN_QUEUE',   '2026-11-21 09:15:00+01')
on conflict ("id") do nothing;
