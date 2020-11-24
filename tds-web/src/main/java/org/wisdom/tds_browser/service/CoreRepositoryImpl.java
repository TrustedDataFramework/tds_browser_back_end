package org.wisdom.tds_browser.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wisdom.tds_browser.bean.Abi;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.bean.Contract;
import org.wisdom.tds_browser.bean.Pair;
import org.wisdom.tds_browser.dao.ContractDao;
import org.wisdom.tds_browser.dao.HeaderDao;
import org.wisdom.tds_browser.dao.SyncHeightDao;
import org.wisdom.tds_browser.dao.TransactionDao;
import org.wisdom.tds_browser.entity.ContractEntity;
import org.wisdom.tds_browser.entity.HeaderEntity;
import org.wisdom.tds_browser.entity.SyncHeightEntity;
import org.wisdom.tds_browser.entity.TransactionEntity;
import org.wisdom.tds_browser.tool.NodeTool;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "core")
@Component
public class CoreRepositoryImpl implements CoreRepository {

    private final HeaderDao headerDao;
    private final TransactionDao transactionDao;
    private final ContractDao contractDao;
    private final SyncHeightDao syncHeightDao;
    private final NodeTool nodeTool;

    public CoreRepositoryImpl(HeaderDao headerDao,
                              ContractDao contractDao,
                              TransactionDao transactionDao,
                              SyncHeightDao syncHeightDao,
                              NodeTool nodeTool) {
        this.headerDao = headerDao;
        this.transactionDao = transactionDao;
        this.contractDao = contractDao;
        this.syncHeightDao = syncHeightDao;
        this.nodeTool = nodeTool;
    }

    @Override
    public Block getBlockByHeight(long height) {
        Optional<HeaderEntity> optional = headerDao.findByHeight(height);
        return optional.map(this::convertBlock).orElse(null);
    }

    @Override
    public String getBinaryByABI(String abi) {
        Optional<ContractEntity> optional = contractDao.findByAbi(abi.getBytes());
        return optional.map(contractEntity -> Hex.toHexString(contractEntity.binary)).orElse(null);
    }

    @Override
    public List<Contract> getContractList() {
        return contractDao.findAll().stream().map(x -> {
            TransactionEntity entity = transactionDao.findById(x.txHash).orElseThrow(RuntimeException::new);
            return Contract.builder()
                    .address(x.address)
                    .amount(x.amount)
                    .from(x.from)
                    .to(x.to)
                    .height(x.height)
                    .txHash(x.txHash)
                    .fee(entity.fee)
                    .createdAt(entity.createdAt)
                    .build();
        }).sorted(Comparator.comparing(Contract::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Block.Transaction> getCallContractList(String address) {
        return transactionDao.findByTypeAndTo(3, address).stream().map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.toHexString(x.payload))
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
                        .build()).sorted(Comparator.comparing(Block.Transaction::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Block.Transaction> getTransactionByFrom(String from) {
        return transactionDao.findByFrom(from).stream().map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.toHexString(x.payload))
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
                        .payload(Hex.toHexString(x.payload))
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
    public Pair<Boolean, Abi> getABIByAddress(String address) {
        Optional<ContractEntity> optional = contractDao.findByAddress(address);
        return optional.map(contractEntity -> Pair.with(true, Abi.builder().abi(JSONObject.parse(new String(contractEntity.abi))).build())).orElseGet(() -> Pair.with(false, null));
    }

    @Override
    public Pair<Boolean, String> uploadContractCode(String code, String address) {
        Optional<ContractEntity> entity = contractDao.findByAddress(address);
        if (!entity.isPresent()) {
            return Pair.with(false, "contract address is not exist");
        }
        if (entity.get().code != null) {
            return Pair.with(false, "code has been exist");
        }
        ContractEntity contractEntity = entity.get();
        contractEntity.code = code.getBytes();
        contractDao.save(contractEntity);
        return Pair.with(true, null);
    }

    @Override
    public Block getBlockByHash(String hash) {
        Optional<HeaderEntity> optional = headerDao.findByBlockHash(hash);
        return optional.map(this::convertBlock).orElse(null);
    }

    @Override
    public Page<Block.Transaction> getTransactionList(Pageable pageable) {
        return transactionDao.findAll(pageable).map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.toHexString(x.payload))
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
                        .build());
    }

    @Override
    public Page<Block> getBlockList(Pageable pageable) {
        Page<HeaderEntity> list = headerDao.findAllByOrderByHeightDesc(pageable);
        return list.map(this::convertBlock);
    }

    @Override
    public Contract getContractByHash(String hash) {
        TransactionEntity entity = transactionDao.findById(hash).orElseThrow(RuntimeException::new);
        ContractEntity x = contractDao.findByTxHash(hash).orElseThrow(RuntimeException::new);
        return Contract.builder()
                .address(x.address)
                .amount(x.amount)
                .from(x.from)
                .to(x.to)
                .height(x.height)
                .txHash(x.txHash)
                .fee(entity.fee)
                .createdAt(entity.createdAt)
                .build();
    }

    @Override
    public Pair<Boolean, String> getPayloadByAddress(String address) {
        Optional<ContractEntity> optional = contractDao.findById(address);
        if (!optional.isPresent()) {
            return Pair.with(false, "contract address is not exist");
        }
        Optional<TransactionEntity> entity = transactionDao.findById(optional.get().txHash);
        return Pair.with(true, entity.map(contractEntity -> Hex.toHexString(transactionDao.findById(contractEntity.txHash).orElseThrow(RuntimeException::new).payload)));
    }

    @Override
    public String getCodeByAddress(String address) {
        Optional<ContractEntity> optional = contractDao.findById(address);
        return optional.map(contractEntity -> {
            if (contractEntity.code == null) {
                return null;
            } else {
                return new String(contractEntity.code);
            }
        }).orElse(null);
    }

    @Override
    public double getAverageRate() {
        SyncHeightEntity entity = syncHeightDao.findBySyncName("block_height");
        HeaderEntity end = headerDao.findByHeight(entity.height).orElseThrow(RuntimeException::new);
        HeaderEntity start = headerDao.findByHeight(1L).orElseThrow(RuntimeException::new);
        return (end.createdAt.getTime() - start.createdAt.getTime()) * 1.0 / 1000 / (entity.height - 1);
    }

    @Override
    public List<Block.Transaction> getTransactionListByAddress(String address) {
        return transactionDao.findByFromOrTo(address, address).stream().sorted(Comparator.comparing(TransactionEntity::getCreatedAt).reversed()).map(x ->
                Block.Transaction.builder()
                        .amount(x.amount)
                        .from(x.from)
                        .gasPrice(x.gasPrice)
                        .nonce(x.nonce)
                        .payload(Hex.toHexString(x.payload))
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
                .collect(Collectors.toList());
    }

    @Override
    public String stat() {
        return nodeTool.stat();
    }

    @Override
    public String pool() {
        return nodeTool.pool();
    }

    @Override
    public String getAccount(String addressOrPublicKey) {
        return nodeTool.getAccount(addressOrPublicKey);
    }

    @Override
    public Block.Transaction getTransactionByTxHash(String txHash) {
        Optional<TransactionEntity> op = transactionDao.findById(txHash);
        if (!op.isPresent()) return null;
        TransactionEntity x = op.get();
        return Block.Transaction.builder()
                .amount(x.amount)
                .from(x.from)
                .gasPrice(x.gasPrice)
                .nonce(x.nonce)
                .payload(Hex.toHexString(x.payload))
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
                .payload(Hex.toHexString(entity.payload))
                .size(entity.size)
                .stateRoot(entity.stateRoot)
                .transactionsRoot(entity.transactionsRoot)
                .version(entity.version)
                .allFee(entities.size() > 0 ? entities.get(0).fee : 0)
                .minerAddress(entities.size() > 0 ? entities.get(0).to : null)
                .body(entities.stream().map(x ->
                        Block.Transaction.builder()
                                .amount(x.amount)
                                .from(x.from)
                                .gasPrice(x.gasPrice)
                                .nonce(x.nonce)
                                .payload(Hex.toHexString(x.payload))
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
