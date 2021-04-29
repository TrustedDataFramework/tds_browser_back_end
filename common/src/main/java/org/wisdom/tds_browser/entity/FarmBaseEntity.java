package org.wisdom.tds_browser.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
public class FarmBaseEntity {
    static final String COLUMN_FARM_BASE_ID = "FarmBaseId";
    static final String COLUMN_BLOCK_HEIGHT = "blockHeight";
    static final String COLUMN_TRANSCATION_HASH = "transcationHash";
    static final String COLUMN_SMAZE_ACCOUNT = "smazeAccount";
    static final String COLUMN_AGE = "age";
    static final String COLUMN_CREATED_AT = "createdAt";
    static final String COLUMN_CHAINID = "chainId";
    static final String COLUMN_ASSET_ADDRESS = "assetAddress";
    static final String COLUMN_TYPE = "type";
    static final String COLUMN_ACCOUNT_ADDRESS = "accountAddress";
    static final String COLUMN_MAPPING_CONTRACT_ADDRESS = "mappingContractAddress";
    static final String COLUMN_OPERATION = "operation";
    static final String COLUMN_START_HEIGHT = "startHeight";
    static final String COLUMN_END_HEIGHT = "endHeight";

    @Column(name = COLUMN_FARM_BASE_ID, nullable = false)
    @Id
    public long FarmBaseId;

    @Column(name = COLUMN_BLOCK_HEIGHT, nullable = false)
    public long blockHeight;

    @Column(name = COLUMN_TRANSCATION_HASH, nullable = false)
    public String transcationHash;

    @Column(name = COLUMN_SMAZE_ACCOUNT, nullable = false)
    public BigDecimal smazeAccount;

    @Column(name = COLUMN_AGE, nullable = false)
    public int age;

    @Column(name = COLUMN_CREATED_AT, nullable = false)
    public Date createdAt;

    @Column(name = COLUMN_CHAINID, nullable = false)
    public String chainId;

    @Column(name = COLUMN_ASSET_ADDRESS, nullable = false)
    public byte[] assetAddress;

    @Column(name = COLUMN_TYPE, nullable = false)
    public int type;

    @Column(name = COLUMN_ACCOUNT_ADDRESS, nullable = false)
    public byte[] accountAddress;

    @Column(name = COLUMN_MAPPING_CONTRACT_ADDRESS)
    public byte[] mappingContractAddress;

    @Column(name = COLUMN_OPERATION)
    public String operation;

    @Column(name = COLUMN_START_HEIGHT)
    public long startHeight;

    @Column(name = COLUMN_END_HEIGHT)
    public long endHeight;
}
