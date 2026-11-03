create table "consignments" (
   "id"      uuid not null,
   "name"    text not null,
   "created" timestamp not null default now(),
   primary key ( "id" )
);
