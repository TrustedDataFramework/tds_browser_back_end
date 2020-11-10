package org.wisdom.tds_browser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.tds_browser.entity.ContractEntity;

import java.util.List;
import java.util.Optional;

public interface ContractDao extends JpaRepository<ContractEntity, String> {

    Optional<ContractEntity> findByAbi(String abi);
}
