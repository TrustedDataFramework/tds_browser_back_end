package org.wisdom.tds_browser.dao;

import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wisdom.tds_browser.entity.FarmBaseEntity;

import java.util.List;
import java.util.Optional;

public interface FarmBaseDao extends JpaRepository<FarmBaseEntity, String> {
    List<FarmBaseEntity> findByAccountAddressAndTypeAndChainIdAndAssetAddress(String accountAddress, int type, int chainId,String assetAddress);
    List<FarmBaseEntity> findByAccountAddressAndTypeAndChainId(String accountAddress, int type, int chainId);

    @Query("select max(m.FarmBaseId) from FarmBaseEntity m")
    Optional<Long> getMaxId();

    Optional<FarmBaseEntity> findByTranscationHashAndAssetAddressAndAccountAddressAndAgeAndTypeAndBlockHeightAndOperation(String transcation,
    String assetAddress,String accountAddress,int age,int type,long blockHeight,String operation);

    List<FarmBaseEntity> findAll();

    @Query("Select max(m.age) from FarmBaseEntity m where m.assetAddress = :assetAddress and m.type = :type and m.accountAddress = :accountAddress and m.operation = 'Mint'")
    Optional<Integer> getMaxAge(String assetAddress, int type, String accountAddress);

    @Query("Select max(m.age) from FarmBaseEntity m where  m.type = :type and m.accountAddress = :accountAddress and m.operation = 'Mint'")
    Optional<Integer> getMaxAgeBylpMaze(int type, String accountAddress);

    @Query("Select m from FarmBaseEntity m where m.assetAddress = :assetAddress and m.type = :type and m.accountAddress = :accountAddress and m.age = :age")
    List<FarmBaseEntity> getByMaxAge(String assetAddress, int type, String accountAddress, int age);

    @Query("Select m from FarmBaseEntity m where m.type = :type and m.accountAddress = :accountAddress and m.age = :age")
    List<FarmBaseEntity> getByMaxAgeBylpMaze(int type, String accountAddress, int age);
}
