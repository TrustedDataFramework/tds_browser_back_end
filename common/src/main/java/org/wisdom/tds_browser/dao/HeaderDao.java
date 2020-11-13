package org.wisdom.tds_browser.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.tds_browser.entity.HeaderEntity;

import java.util.List;
import java.util.Optional;

public interface HeaderDao extends JpaRepository<HeaderEntity, String> {

    Page<HeaderEntity> findAllByOrderByHeightDesc(Pageable pageable);

    List<HeaderEntity> findByHeightBetween(long start, long end, Pageable pageable);

    Optional<HeaderEntity> findByHeight(long height);

    Optional<HeaderEntity> findByBlockHash(String hash);
}
