insert into "cargo_types" ("id", "name")
values (1, 'WOOD'),
       (2, 'COAL'),
       (3, 'IRON_ORE'),
       (4, 'BULK_MATERIALS'),
       (5, 'LIQUID_CHEMICALS')
on conflict ("name") do nothing;
