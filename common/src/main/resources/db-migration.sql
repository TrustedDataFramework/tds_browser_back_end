DROP TABLE IF EXISTS "sync_height";
create table  "sync_height"
(
    id varchar(32) primary key,
    sync_name varchar(100) not null,
    height int4 default null,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp  default null
);

DROP TABLE IF EXISTS "header";

CREATE TABLE if not exists "header" (
"block_hash" varchar(255) primary key,
"hash_prev" varchar(255) NOT NULL,
"tx_root" varchar(255) NOT NULL,
"state_root" varchar(255) NOT NULL,
"height" int8 NOT NULL,
"extra_data" varchar(2000),
"block_size" int8,
created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS "contract";

CREATE TABLE if not exists "contract" (
  "address" varchar(255) primary key,
  "abi" bytea DEFAULT NULL,
  "amount" int8 NOT NULL,
  "binary" bytea NOT NULL,
  "code" bytea,
  "from" varchar(255),
  "height" int8 NOT NULL,
  "to" varchar(255),
  "tx_hash" varchar(255),
  created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS "transaction";

CREATE TABLE if not exists "transaction" (
"tx_hash" varchar(255) primary key,
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
created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

