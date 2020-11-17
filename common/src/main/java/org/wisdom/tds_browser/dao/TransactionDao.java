package org.wisdom.tds_browser.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.tds_browser.entity.TransactionEntity;

import java.util.List;

public interface TransactionDao extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByBlockHash(String blockHash);

    List<TransactionEntity> findByFrom(String from);

    List<TransactionEntity> findByTo(String to);

    List<TransactionEntity> findByTypeAndTo(int type, String to);

}
