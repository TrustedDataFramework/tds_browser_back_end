package org.wisdom.tds_browser.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wisdom.tds_browser.bean.Abi;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.bean.Contract;
import org.wisdom.tds_browser.bean.Pair;

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

    Pair<Boolean, String> uploadContractCode(String code, String address);

    Block getBlockByHash(String hash);

    Page<Block.Transaction> getTransactionList(Pageable pageable);

    Page<Block> getBlockList(Pageable pageable);

    Contract getContractByHash(String hash);

    Pair<Boolean, String>  getPayloadByAddress(String address);

    String getCodeByAddress(String address);

    double getAverageRate();

    List<Block.Transaction> getTransactionListByAddress(String address);
}
