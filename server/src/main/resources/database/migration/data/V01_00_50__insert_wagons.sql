-- WOOD -> Roos, COAL -> Eaos, IRON_ORE -> Falns, BULK_MATERIALS -> Tadns, LIQUID_CHEMICALS -> Zacns
insert into "wagons" ("id", "name", "tare_weight", "net_capacity_weight", "gross_weight_limit", "volume_capacity",
                      "rent_price")
values (1, 'Roos', 23.0, 67.0, 90.0, 80.0, 14.0),
       (2, 'Eaos', 22.0, 58.0, 80.0, 72.0, 15.0),
       (3, 'Falns', 25.0, 65.0, 90.0, 75.0, 16.5),
       (4, 'Tadns', 24.0, 66.0, 90.0, 72.0, 15.5),
       (5, 'Zacns', 23.0, 67.0, 90.0, 95.0, 18.0)
on conflict ("id") do nothing;
