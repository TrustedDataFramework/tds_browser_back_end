package org.wisdom.tds_browser.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wisdom.tds_browser.bean.*;

import java.io.IOException;
import java.util.List;

public interface CoreRepository {

    Block getBlockByHeight(long height);

    String getBinaryByABI(String abi);

    List<Contract> getContractList();

    List<Block.Transaction> getCallContractList(String address);

    Block.Transaction getTransactionByTxHash(String txHash);

    List<Block.Transaction> getTransactionByFrom(String from);

    List<Block.Transaction> getTransactionByTo(String to);

    Pair<Boolean, Abi> getABIByAddress(String address);

    Pair<Boolean, String> uploadContractCode(String code, String address) throws IOException;

    Block getBlockByHash(String hash);

    Page<Block.Transaction> getTransactionList(Pageable pageable);

    Page<Block> getBlockList(Pageable pageable);

    Contract getContractByHash(String hash);

    Pair<Boolean, String>  getPayloadByAddress(String address);

    String getCodeByAddress(String address);

    double getAverageRate();

    Page<Block.Transaction> getTransactionListByAddress(String address, Pageable pageable);

    String stat();

    String pool();

    String getAccount(String addressOrPublicKey);

    String getVersion();

    List<MazeProfit> getFarmnaseMazeProfitList(String txId,String accountAddress,int type,String assetAddress);

    List<MazeProfit> getAll();

}
