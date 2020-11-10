package org.wisdom.tds_browser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.tds_browser.entity.SyncHeightEntity;

public interface SyncHeightDao extends JpaRepository<SyncHeightEntity, Integer> {

    SyncHeightEntity findBySyncName(String syncName);

}
