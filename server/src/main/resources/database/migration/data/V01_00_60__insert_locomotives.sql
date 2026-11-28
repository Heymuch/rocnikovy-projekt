insert into "locomotives" ("id", "name", "max_towing_weight", "max_speed", "fixed_rent_price")
values (1, 'CZ LOKO EffiShunter 1000',   1200.0, 100.0, 18000.0),
       (2, 'ČD Cargo 363.5 (Eso)',       2000.0, 120.0, 26000.0),
       (3, 'Bombardier TRAXX F140 MS',   2500.0, 140.0, 38000.0),
       (4, 'Škoda 109E (Emil Zátopek)',  2000.0, 200.0, 42000.0),
       (5, 'Siemens Vectron MS',         3200.0, 160.0, 48000.0)
on conflict ("id") do nothing;
