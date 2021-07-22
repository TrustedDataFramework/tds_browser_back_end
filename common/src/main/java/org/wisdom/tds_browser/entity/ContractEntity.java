package org.wisdom.tds_browser.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = ContractEntity.TABLE_CONTRACT, indexes = {
        @Index(name = "contract_hash_index", columnList = ContractEntity.COLUMN_TX_HASH),
})
public class ContractEntity {

    static final String TABLE_CONTRACT = "contract";
    static final String COLUMN_HEIGHT = "height";
    static final String COLUMN_BINARY = "[binary]";
    static final String COLUMN_ABI = "abi";
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_TX_TO = "[to]";
    static final String COLUMN_TX_FROM = "[from]";
    static final String COLUMN_TX_HASH = "tx_hash";
    static final String COLUMN_CODE = "code";
    static final String COLUMN_ADDRESS = "address";
    static final String COLUMN_TX_AMOUNT = "amount";

    @Column(name = COLUMN_ADDRESS, nullable = false)
    @Id
    public String address;

    @Column(name = COLUMN_TX_HASH, nullable = false)
    public String txHash;


    @Column(name = COLUMN_BINARY, length = Integer.MAX_VALUE)
    public byte[] binary;

    @Column(name = COLUMN_ABI)
    public byte[] abi;

    @Column(name = COLUMN_CODE, length = Integer.MAX_VALUE)
    public byte[] code;

    @Column(name = COLUMN_HEIGHT, nullable = false)
    public long height;

    @Column(name = COLUMN_TX_FROM)
    public String from;

    @Column(name = COLUMN_TX_TO)
    public String to;

    @Column(name = COLUMN_TX_AMOUNT)
    public long amount;

    @Column(name = COLUMN_CREATED_AT)
    public Date createdAt;
}
