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
    static final String COLUMN_TX_FROM = "[from]";
    static final String COLUMN_TX_NONCE = "nonce";
    static final String COLUMN_TX_TO = "[to]";
    static final String COLUMN_TX_TYPE = "type";
    static final String TABLE_TRANSACTION = "transaction";
    static final String COLUMN_BLOCK_HASH = "block_hash";
    static final String COLUMN_TX_GAS_PRICE = "gas_price";
    static final String COLUMN_TX_GAS = "gas";
    static final String COLUMN_TX_AMOUNT = "amount";
    static final String COLUMN_TX_CREATED_AT = "created_at";
    static final String COLUMN_TX_POSITION = "position";
    static final String COLUMN_TX_RAW = "raw";
    static final String COLUMN_TX_FEE = "fee";

    static final String COLUMN_TX_SIZE = "size";

    static final String COLUMN_BLOCK_HEIGHT = "height";

    static final String COLUMN_TX_R = "r";
    static final String COLUMN_TX_S = "s";
    static final String COLUMN_TX_V = "v";

    static final String COLUMN_TYPE = "type";

    @Column(name = COLUMN_TYPE, nullable = false)
    public long type;

    @Column(name = COLUMN_BLOCK_HASH, nullable = false)
    public String blockHash;

    @Column(name = COLUMN_TX_HASH, nullable = false)
    @Id
    public String txHash;

    @Column(name = COLUMN_TX_FROM)
    public String from;

    @Column(name = COLUMN_TX_TO)
    public String to;

    @Column(name = COLUMN_TX_R)
    public String r;

    @Column(name = COLUMN_TX_S)
    public String s;

    @Column(name = COLUMN_TX_V)
    public long v;

    @Column(name = COLUMN_TX_GAS_PRICE)
    public String gasPrice;

    @Column(name = COLUMN_TX_GAS)
    public String gas;

    @Column(name = COLUMN_TX_AMOUNT)
    public long amount;

    @Column(name = COLUMN_TX_POSITION)
    public int position;

    @Column(name = COLUMN_TX_RAW)
    public String raw;

    @Column(name = COLUMN_TX_NONCE)
    public String nonce;

    @Column(name = COLUMN_TX_CREATED_AT)
    public Date createdAt;

    @Column(name = COLUMN_BLOCK_HEIGHT, nullable = false)
    public long height;

}
