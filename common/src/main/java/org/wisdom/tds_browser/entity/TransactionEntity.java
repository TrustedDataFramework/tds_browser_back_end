package org.wisdom.tds_browser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = TransactionEntity.TABLE_TRANSACTION, indexes = {
        @Index(name = "transaction_type_index", columnList = TransactionEntity.COLUMN_TX_TYPE),
        @Index(name = "transaction_hash_index", columnList = TransactionEntity.COLUMN_TX_HASH),
        @Index(name = "transaction_from_index", columnList = TransactionEntity.COLUMN_TX_FROM),
        @Index(name = "transaction_to_index", columnList = TransactionEntity.COLUMN_TX_TO)
})
public class TransactionEntity {

    static final String COLUMN_TX_HASH = "tx_hash";
    static final String COLUMN_TX_VERSION = "version";
    static final String COLUMN_TX_FROM = "[from]";
    static final String COLUMN_TX_NONCE = "nonce";
    static final String COLUMN_TX_TO = "[to]";
    static final String COLUMN_TX_SIGNATURE = "signature";
    static final String COLUMN_TX_TYPE = "type";
    static final String TABLE_TRANSACTION = "transaction";
    static final String COLUMN_BLOCK_HASH = "block_hash";
    static final String COLUMN_TX_GAS_PRICE = "gas_price";
    static final String COLUMN_TX_GAS_LIMIT = "gas_limit";
    static final String COLUMN_TX_AMOUNT = "amount";
    static final String COLUMN_TX_CREATED_AT = "created_at";
    static final String COLUMN_TX_POSITION = "position";
    static final String COLUMN_TX_PAYLOAD = "payload";
    static final String COLUMN_TX_FEE = "fee";

    static final String COLUMN_TX_SIZE = "size";

    @Column(name = COLUMN_BLOCK_HASH, nullable = false)
    public String blockHash;

    @Column(name = COLUMN_TX_HASH, nullable = false)
    @Id
    public String txHash;

    @Column(name = COLUMN_TX_VERSION, nullable = false)
    public int version;

    @Column(name = COLUMN_TX_FROM, nullable = false)
    public String from;

    @Column(name = COLUMN_TX_TO, nullable = false)
    public String to;

    @Column(name = COLUMN_TX_SIGNATURE, nullable = false)
    public String signature;

    @Column(name = COLUMN_TX_TYPE, nullable = false)
    public int type;

    @Column(name = COLUMN_TX_GAS_PRICE, nullable = false)
    public long gasPrice;

    @Column(name = COLUMN_TX_GAS_LIMIT, nullable = false)
    public long gasLimit;

    @Column(name = COLUMN_TX_AMOUNT, nullable = false)
    public long amount;

    @Column(name = COLUMN_TX_POSITION, nullable = false)
    public int position;

    @Column(name = COLUMN_TX_PAYLOAD, nullable = false, length = Short.MAX_VALUE)
    public byte[] payload;

    @Column(name = COLUMN_TX_FEE, nullable = false)
    public long fee;

    @Column(name = COLUMN_TX_SIZE, nullable = false)
    public long size;

    @Column(name = COLUMN_TX_NONCE, nullable = false)
    public long nonce;

    @Column(name = COLUMN_TX_CREATED_AT, nullable = false)
    public Date createdAt;

}
