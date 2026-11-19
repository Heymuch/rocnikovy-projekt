-- Rozšíření, která nejsou "trusted" a smí je vytvořit jen superuživatel (role flyway to nesmí).
-- Spouští se jen při první inicializaci datového adresáře.
create extension if not exists "pgrouting" cascade; -- vyžaduje postgis
