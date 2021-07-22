create table if not exists sync_height(
    id serial primary key,
    sync_name varchar(100) not null,
    height int4 default null,
    created_at TIMESTAMP(0)  without time zone default (now() at time zone 'utc'),
    updated_at TIMESTAMP(0)  without time zone default (now() at time zone 'utc'),
    deleted_at TIMESTAMP(0)  default null
);

create unique index if not exists sync_name_uindex
    on sync_height (sync_name);

INSERT INTO "public"."sync_height" ( "sync_name", "height", "created_at", "updated_at" ) VALUES ( 'block_height', 0 , now(),now()) ON conflict("sync_name")  DO NOTHING;


CREATE TABLE if not exists "header" (
"block_hash" varchar(255) NOT NULL,
"hash_prev" varchar(255) NOT NULL,
"tx_root" varchar(255) NOT NULL,
"state_root" varchar(255) NOT NULL,
"height" int8 NOT NULL,
"extra_data" varchar(2000),
"block_size" int8,
"created_at" TIMESTAMP(0)  without time zone default (now() at time zone 'utc'),
CONSTRAINT "header_pkey" PRIMARY KEY ( "block_hash" )
);

create unique index if not exists height_uindex
    on header (height);

create unique index if not exists block_hash_uindex
    on header (block_hash);

CREATE TABLE if not exists "transaction" (
"tx_hash" varchar(255) NOT NULL,
"type" int4,
"nonce" varchar(255),
"from" varchar(255),
"amount" int8,
"position" int8,
"block_hash" varchar(255) NOT NULL,
"height" int8 NOT NULL,
"gas_price" varchar(255),
"gas" varchar(255),
"raw" varchar(2000),
"r" varchar(2000),
"s" varchar(2000),
"v" int8,
"to" varchar(255),
"created_at" TIMESTAMP(0)  without time zone default (now() at time zone 'utc'),
CONSTRAINT "transaction_pkey" PRIMARY KEY ( "tx_hash" )
);

create unique index if not exists transaction_tx_hash_uindex
    on transaction (tx_hash);

CREATE TABLE if not exists "contract" (
  "address" varchar(255) NOT NULL,
  "abi" bytea DEFAULT NULL,
  "amount" int8 NOT NULL,
  "binary" bytea NOT NULL,
  "code" bytea,
  "from" varchar(255),
  "height" int8 NOT NULL,
  "to" varchar(255),
  "tx_hash" varchar(255),
  "created_at" TIMESTAMP(0)  without time zone default (now() at time zone 'utc'),
  CONSTRAINT "contract_pkey" PRIMARY KEY ("address")
);