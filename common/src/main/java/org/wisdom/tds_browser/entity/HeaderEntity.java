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
@Table(name = HeaderEntity.TABLE_HEADER, indexes = {
        @Index(name = "header_hash_prev_index", columnList = HeaderEntity.COLUMN_HASH_PREV),
        @Index(name = "header_height_index", columnList = HeaderEntity.COLUMN_HEIGHT),
})
public class HeaderEntity {

    static final String TABLE_HEADER = "header";
    static final String COLUMN_HASH = "block_hash";
    static final String COLUMN_HEIGHT = "height";
    static final String COLUMN_HASH_PREV = "hash_prev";
    static final String COLUMN_VERSION = "version";
    static final String COLUMN_TRANSACTIONS_ROOT = "tx_root";
    static final String COLUMN_STATE_ROOT = "state_root";
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_PAYLOAD = "payload";
    static final String COLUMN_SIZE = "block_size";


    @Column(name = COLUMN_HASH, nullable = false)
    @Id
    public String blockHash;

    @Column(name = COLUMN_VERSION, nullable = false)
    public long version;

    @Column(name = COLUMN_HASH_PREV, nullable = false)
    public String hashPrevBlock;

    @Column(name = COLUMN_HEIGHT, nullable = false)
    public long height;

    @Column(name = COLUMN_SIZE, nullable = false)
    public long size;

    @Column(name = COLUMN_TRANSACTIONS_ROOT, nullable = false)
    public String transactionsRoot;

    @Column(name = COLUMN_STATE_ROOT, nullable = false)
    public String stateRoot;

    @Column(name = COLUMN_PAYLOAD, nullable = false, length = Short.MAX_VALUE)
    public byte[] payload;

    @Column(name = COLUMN_CREATED_AT, nullable = false)
    public Date createdAt;

}
