-- WOOD -> Roos, COAL -> Eaos, IRON_ORE -> Falns, BULK_MATERIALS -> Tadns, LIQUID_CHEMICALS -> Zacns
insert into "wagons" ("id", "name", "tare_weight", "net_capacity_weight", "gross_weight_limit", "volume_capacity",
                      "rent_price", "fixed_rent_price")
values (1, 'Roos',  24.5, 65.5, 90.0, 115.0, 10.0, 1800.0),
       (2, 'Eaos',  22.0, 58.0, 80.0,  72.0,  9.5, 2000.0),
       (3, 'Falns', 24.5, 65.5, 90.0,  75.0, 12.0, 2400.0),
       (4, 'Tadns', 24.5, 65.5, 90.0,  76.0, 12.5, 2200.0),
       (5, 'Zacns', 23.5, 66.5, 90.0,  95.0, 16.5, 3200.0)
on conflict ("id") do nothing;
