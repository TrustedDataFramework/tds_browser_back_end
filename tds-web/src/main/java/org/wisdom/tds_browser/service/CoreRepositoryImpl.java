package org.wisdom.tds_browser.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.bean.Contract;
import org.wisdom.tds_browser.dao.ContractDao;
import org.wisdom.tds_browser.dao.HeaderDao;
import org.wisdom.tds_browser.dao.TransactionDao;
import org.wisdom.tds_browser.entity.ContractEntity;
import org.wisdom.tds_browser.entity.HeaderEntity;
import org.wisdom.tds_browser.entity.TransactionEntity;
import org.wisdom.tds_browser.tool.NodeTool;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "core")
@Component
public class CoreRepositoryImpl implements CoreRepository {

    private HeaderDao headerDao;
    private TransactionDao transactionDao;
    private NodeTool nodeTool;
    private ContractDao contractDao;

    public CoreRepositoryImpl(NodeTool nodeTool,
                              HeaderDao headerDao,
                              ContractDao contractDao,
                              TransactionDao transactionDao) {
        this.headerDao = headerDao;
        this.nodeTool = nodeTool;
        this.transactionDao = transactionDao;
        this.contractDao = contractDao;
    }

    @Override
    public Block getBlockByHeight(long height) {
        Optional<HeaderEntity> optional = headerDao.findByHeight(height);
        return optional.map(this::convertBlock).orElse(null);
    }

    @Override
    public String getBinaryByABI(String abi) {
        Optional<ContractEntity> optional = contractDao.findByAbi(abi);
        return optional.map(contractEntity -> contractEntity.binary).orElse(null);
    }

    @Override
    public List<Contract> getContractList() {
        return contractDao.findAll().stream().map(x -> Contract.builder()
                .address(x.address)
                .amount(x.amount)
                .from(x.from)
                .to(x.to)
                .height(x.height)
                .txHash(x.txHash)
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<Block.Transaction> getCallContractList(String address) {
        return transactionDao.findByTypeAndTo(3, address).stream().map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.encodeHexString(x.payload))
                        .signature(x.signature)
                        .hash(x.txHash)
                        .type(x.type)
                        .version(x.version)
                        .to(x.to)
                        .fee(x.fee)
                        .createdAt(x.createdAt)
                        .gasLimit(x.gasLimit)
                        .position(x.position)
                        .size(x.size)
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<Block.Transaction> getTransactionByFrom(String from) {
        return transactionDao.findByFrom(from).stream().map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.encodeHexString(x.payload))
                        .signature(x.signature)
                        .hash(x.txHash)
                        .type(x.type)
                        .version(x.version)
                        .to(x.to)
                        .fee(x.fee)
                        .createdAt(x.createdAt)
                        .gasLimit(x.gasLimit)
                        .position(x.position)
                        .size(x.size)
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<Block.Transaction> getTransactionByTo(String to) {
        return transactionDao.findByTo(to).stream().map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.encodeHexString(x.payload))
                        .signature(x.signature)
                        .hash(x.txHash)
                        .type(x.type)
                        .version(x.version)
                        .to(x.to)
                        .fee(x.fee)
                        .createdAt(x.createdAt)
                        .gasLimit(x.gasLimit)
                        .position(x.position)
                        .size(x.size)
                        .build()).collect(Collectors.toList());
    }

    @Override
    public Block.Transaction getTransactionByTxHash(String txHash) {
        Optional<TransactionEntity> op = transactionDao.findById(txHash);
        if (!op.isPresent()) return null;
        TransactionEntity x =op.get();
        return Block.Transaction.builder()
                .amount(x.amount)
                .from(x.from)
                .gasPrice(x.gasPrice)
                .nonce(x.nonce)
                .payload(Hex.encodeHexString(x.payload))
                .signature(x.signature)
                .hash(x.txHash)
                .type(x.type)
                .version(x.version)
                .to(x.to)
                .fee(x.fee)
                .createdAt(x.createdAt)
                .gasLimit(x.gasLimit)
                .position(x.position)
                .size(x.size)
                .build();
    }

    private Block convertBlock(HeaderEntity entity) {
        List<TransactionEntity> entities = transactionDao.findByBlockHash(entity.blockHash);
        return Block.builder()
                .createdAt(entity.getCreatedAt())
                .hash(entity.blockHash)
                .hashPrev(entity.hashPrevBlock)
                .height(entity.height)
                .payload(Hex.encodeHexString(entity.payload))
                .size(entity.size)
                .stateRoot(entity.stateRoot)
                .transactionsRoot(entity.transactionsRoot)
                .version(entity.version)
                .body(entities.stream().map(x ->
                        Block.Transaction.builder()
                                .amount(x.amount)
                                .from(x.from)
                                .gasPrice(x.gasPrice)
                                .nonce(x.nonce)
                                .payload(Hex.encodeHexString(x.payload))
                                .signature(x.signature)
                                .hash(x.txHash)
                                .type(x.type)
                                .version(x.version)
                                .to(x.to)
                                .fee(x.fee)
                                .createdAt(x.createdAt)
                                .gasLimit(x.gasLimit)
                                .position(x.position)
                                .size(x.size)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
