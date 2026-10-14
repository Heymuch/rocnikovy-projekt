# Databáze

## Databázový model
```mermaid
    %%{init: {"layout": "elk"}}%%
    erDiagram

    ORDER {
        uuid id PK
        long user_id FK
        uuid? train_set_id FK "Vlaková souprava (může být null, pokud je ve frontě)"
        long cargo_type_id FK "Odkaz na typ nákladu z číselníku"
        uuid station_from FK "Počáteční stanice (místo nakládky)"
        uuid station_to FK "Konečná stanice (místo vykládky)"
        uuid[] route "Sled bodů trasy"
        string status "Stav objednávky, enum (Čeká ve frontě, Potvrzená, V přepravě, Dokončena)"
        string code "Kód objednávky"
        float volume "Objem konkrétního nákladu (m3)"
        float weight "Hmotnost konkrétního nákladu (t)"
        long wagon_id FK "Typ wagónu"
        int wagon_count "Počet potřebných wagónů"
        string customer_email "E-mail zákazníka"
        string customer_name "Jméno zákazníka"
        date load_date "Začátek nakládání"
        date unload_date "Začátek doby vykládání"
        date preferred_departure "Preferovaný odjezd"
        date preferred_arrival "Preferovaný příjezd"
        date total_price "Cena přepravy (Kč)"
    }

    WAGON {
        long id PK
        string name "Označení vagónu"
        float tare_weight "Tara - vlastní hmotnost prázdného vagónu (t)"
        float net_capacity_weight "Netto - maximální nosnost nákladu (t)"
        float gross_weight_limit "Brutto - maximální celková hmotnost (t)"
        float volume_capacity "Maximální objem nákladu (m3)"
        float rent_price "Cena za pronájem (Kč/km)"
    }

    TRAIN {
        long id PK
        string name "Název/označení lokomotivy"
        integer max_towing_weight "Maximální tažená hmotnost (t)"
        integer max_speed "Maximální rychlost v km/h"
        float rent_price "Cena za pronájem (Kč/km)"
    }

    TRAIN_SET {
        uuid id PK
        long locomotive_id FK
        date departure_date "Čas odjezdu"
        date arrival_date "Čas příjezdu"
        date expiration_date "Expirace soupravy"
    }


    STATION {
        uuid id PK, FK
        varchar name UK
    }

    CARGO_TYPE {
        long id PK
        string name UK "Typ nákladu (např. uhlí, dřevo)"
    }

    RAILWAY_GRAPH {
        uuid node_a_id FK
        uuid node_b_id FK
        int distance "Vzdálenost mezi body v m"
    }

    RAILWAY_NODE {
        uuid id PK
        float latitude
        float longitude
    }


%% Vztahy

%% TRAIN má nula až víc TRAIN SET. TRAIN SET má vždy jednu TRAIN.
    TRAIN ||--o{ TRAIN_SET : "táhne"

%% ROUTE má dvě až více stanic (značeno jako 1..N), STATION má nula až více ROUTE
    STATION ||--o{ ORDER : "nakládá se v"
    STATION ||--o{ ORDER : "vykládá se v"

%% CARGO_TYPE má nula nebo více ORDER. ORDER má právě jednu CARGO_TYPE.
    CARGO_TYPE ||--o{ ORDER : "kategorizuje náklad"
    WAGON ||--o{ ORDER : ""

%% TRAIN SET má jedna až více ORDER, ORDER má nula až jeden TRAIN SET.
    TRAIN_SET |o--|{ ORDER : "převáží"

    RAILWAY_NODE ||--o{ RAILWAY_GRAPH : "je součástí"
    RAILWAY_NODE ||--o{ RAILWAY_GRAPH : "je součástí"
    RAILWAY_NODE ||--o| STATION : "je přiřazen"
```

