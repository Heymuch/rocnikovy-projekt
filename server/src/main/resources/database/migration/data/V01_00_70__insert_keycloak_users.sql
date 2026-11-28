insert into "users" ("id", "email", "name", "is_blocked")
values ('a1b05d0e-0000-4000-8000-000000000001', 'albus.dumbledore@example.com', 'albus.dumbledore', false),
       ('a1b05d0e-0000-4000-8000-000000000002', 'harry.potter@example.com', 'harry.potter', false),
       ('a1b05d0e-0000-4000-8000-000000000003', 'ron.weasley@example.com', 'ron.weasley', false)
on conflict ("id") do nothing;
