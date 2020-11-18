package org.wisdom.tds_browser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.tds_browser.entity.ContractEntity;

import java.util.Optional;

public interface ContractDao extends JpaRepository<ContractEntity, String> {

    Optional<ContractEntity> findByAbi(byte[] abi);

    Optional<ContractEntity> findByAddress(String address);

    Optional<ContractEntity> findByTxHash(String txHash);
}
