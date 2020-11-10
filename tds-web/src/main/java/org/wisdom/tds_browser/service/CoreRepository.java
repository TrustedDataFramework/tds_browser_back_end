package org.wisdom.tds_browser.service;

import org.springframework.data.domain.Page;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.bean.Contract;

import java.util.List;

public interface CoreRepository {

    Block getBlockByHeight(long height);

    String getBinaryByABI(String abi);

    List<Contract> getContractList();

    List<Block.Transaction> getCallContractList(String address);

    Block.Transaction getTransactionByTxHash(String txHash);

    List<Block.Transaction> getTransactionByFrom(String from);

    List<Block.Transaction> getTransactionByTo(String to);
}
