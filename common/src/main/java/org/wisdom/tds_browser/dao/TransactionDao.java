package org.wisdom.tds_browser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.tds_browser.entity.TransactionEntity;

import java.util.List;
import java.util.Optional;

public interface TransactionDao extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByBlockHash(String blockHash);

    List<TransactionEntity> findByFrom(String from);

    List<TransactionEntity> findByTo(String to);

    List<TransactionEntity> findByTypeAndTo(int type, String to);

}
