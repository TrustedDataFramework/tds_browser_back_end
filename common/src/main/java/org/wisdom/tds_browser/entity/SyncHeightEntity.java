package org.wisdom.tds_browser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = SyncHeightEntity.TABLE_SYNC_HEIGHT)
@SQLDelete(sql = "update coin set deleted_at = now() at time zone 'utc' where id = ?")
@Where(clause = "deleted_at is null")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncHeightEntity {

    static final String TABLE_SYNC_HEIGHT = "sync_height";

    static final String COLUMN_ID = "id";

    static final String COLUMN_SYNC_NAME = "sync_name";

    static final String COLUMN_HEIGHT = "height";

    static final String COLUMN_CREATED_AT = "created_at";

    static final String COLUMN_UPDATED_AT = "updated_at";

    static final String COLUMN_DELETED_AT = "deleted_at";

    @Column(name = COLUMN_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    public int id;

    @Column(name = COLUMN_SYNC_NAME, nullable = false)
    public String syncName;

    @Column(name = COLUMN_HEIGHT, nullable = false)
    public Long height;

    @Column(name = COLUMN_CREATED_AT)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    public Date createdAt;

    @Column(name = COLUMN_UPDATED_AT)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    public Date updatedAt;

    @Column(name = COLUMN_DELETED_AT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date deletedAt;

}
