package org.wisdom.tds_browser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wisdom.tds_browser.entity.FarmBaseEntity;

import java.util.List;
import java.util.Optional;

public interface FarmBaseDao extends JpaRepository<FarmBaseEntity, String> {
    List<FarmBaseEntity> findByAccountAddressAndTypeAndChainIdAndAssetAddress(byte[] accountAddress, int type, String txid,byte[] assetAddress);

    @Query("select max(m.FarmBaseId) from FarmBaseEntity m")
    Optional<Long> getMaxId();

    Optional<FarmBaseEntity> findByTranscationHashAndAssetAddressAndAccountAddressAndAgeAndTypeAndBlockHeightAndOperation(String transcation,
    byte[] assetAddress,byte[] accountAddress,int age,int type,long blockHeight,String operation);

    List<FarmBaseEntity> findAll();
}
