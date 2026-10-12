# Databáze

## Databázový model
```mermaid
    %%{init: {"layout": "elk"}}%%
    erDiagram

    ORDER {
        uuid id PK
        Long user_id FK
        uuid? train_set_id FK "Vlaková souprava (může být null, pokud je ve frontě)"
        long cargo_type_id FK "Odkaz na typ nákladu z číselníku"
        Long route_id FK "Přiřazená trasa přepravy"
        uuid station_from FK "Počáteční stanice (místo nakládky)"
        uuid station_to FK "Konečná stanice (místo vykládky)"
        String status "Stav objednávky, enum (Čeká ve frontě, Potvrzená, V přepravě, Dokončena)"
        String name "Název objednávky"
        Float volume "Objem konkrétního nákladu (m3)"
        Float weight "Hmotnost konkrétního nákladu (t)"
        Timestamp startLoadTime "Začátek doby nakládání"
        Timestamp endLoadTime "Konec doby nakládání"
        Timestamp startUnloadTime "Začátek doby vykládání"
        Timestamp endUnloadTime "Konec doby vykládání"
        Timestamp preferredDepartureTime "Preferovaný čas odjezdu"
        Timestamp preferredArrivalTime "Preferovaný čas příjezdu"
        Float price "Cena přepravy (Kč)"
    }
    
    USER {
        Long id PK
        String username UK
        String password
    }

    WAGON {
        Long id PK
        String name "Označení vagónu"
        Float tareWeight "Tara - vlastní hmotnost prázdného vagónu (t)"
        Float netCapacityWeight "Netto - maximální nosnost nákladu (t)"
        Float grossWeightLimit "Brutto - maximální celková hmotnost (t)"
        Float volumeCapacity "Maximální objem nákladu (m3)"
        Float rent "Cena za pronájem vagónu (Kč)"
    }

    ORDER_WAGON {
        Long id PK
        Long order_id FK
        Long wagon_id FK
    }

    TRAIN {
        long id PK
        string name "Název/označení lokomotivy"
        integer max_towing_weight "Maximální tažená hmotnost (t)"
        integer max_speed "Maximální rychlost v km/h"
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

    ROUTE {
        uuid id PK
        Float length "Délka trasy v km"
        Integer estimatedTime "Odhadovaný čas projetí (např. v minutách)"
    }

    ROUTE_STATION {
        Long id PK
        Long route_id FK
        Long station_id FK
        Integer stationOrder "Pořadí stanice na trase (1, 2, 3...)"
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

%% USER má nula až víc ORDER. ORDER má právě jednoho USER.
    USER ||--o{ ORDER : "vytváří"

%% TRAIN má nula až víc TRAIN SET. TRAIN SET má vždy jednu TRAIN.
    TRAIN ||--o{ TRAIN_SET : "táhne"

%% ROUTE má dvě až více stanic (značeno jako 1..N), STATION má nula až více ROUTE
    ROUTE ||--|{ ROUTE_STATION : "skládá se z"
    STATION ||--o{ ROUTE_STATION : "je bodem na"

%% CARGO_TYPE má nula nebo více ORDER. ORDER má právě jednu CARGO_TYPE.
    CARGO_TYPE ||--o{ ORDER : "kategorizuje náklad"

%% ORDER má právě jednu ROUTE. ROUTE má jednu až víc ORDER.
    ROUTE ||--|{ ORDER : "je naplánována pro"

%% ORDER má jeden až více WAGON a WAGON má nula až více ORDER
    ORDER ||--|{ ORDER_WAGON : "má přidělené"
    WAGON ||--o{ ORDER_WAGON : "je součástí"

%% TRAIN SET má jedna až více ORDER, ORDER má nula až jeden TRAIN SET.
    TRAIN_SET |o--|{ ORDER : "převáží"

    RAILWAY_NODE ||--o{ RAILWAY_GRAPH : "je součástí"
    RAILWAY_NODE ||--o{ RAILWAY_GRAPH : "je součástí"
    RAILWAY_NODE ||--o| STATION : "je přiřazen"
```

