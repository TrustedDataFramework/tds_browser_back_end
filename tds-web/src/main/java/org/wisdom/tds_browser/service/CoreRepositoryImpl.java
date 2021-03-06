package org.wisdom.tds_browser.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.wisdom.tds_browser.bean.*;
import org.wisdom.tds_browser.dao.*;
import org.wisdom.tds_browser.entity.*;
import org.wisdom.tds_browser.tool.NodeTool;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j(topic = "core")
@Component
public class CoreRepositoryImpl implements CoreRepository {

    private final HeaderDao headerDao;
    private final TransactionDao transactionDao;
    private final ContractDao contractDao;
    private final SyncHeightDao syncHeightDao;
    private final NodeTool nodeTool;
    private final FarmBaseDao farmBaseDao;

    public CoreRepositoryImpl(HeaderDao headerDao,
                              ContractDao contractDao,
                              TransactionDao transactionDao,
                              SyncHeightDao syncHeightDao,
                              NodeTool nodeTool, FarmBaseDao farmBaseDao) {
        this.headerDao = headerDao;
        this.transactionDao = transactionDao;
        this.contractDao = contractDao;
        this.syncHeightDao = syncHeightDao;
        this.nodeTool = nodeTool;
        this.farmBaseDao = farmBaseDao;
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
        }).sorted(Comparator.comparing(Contract::getHeight).reversed()).collect(Collectors.toList());
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
                        .blockHeight(headerDao.findByBlockHash(x.blockHash).orElseThrow(RuntimeException::new).height)
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
                        .blockHeight(headerDao.findByBlockHash(x.blockHash).orElseThrow(RuntimeException::new).height)
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
        return transactionDao.findByTypeNot(0, pageable).map(x ->
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
                        .blockHeight(x.height)
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
        if (entity.height == 0){
            return 0;
        }
        HeaderEntity end = headerDao.findByHeight(entity.height).orElseThrow(RuntimeException::new);
        HeaderEntity start = headerDao.findByHeight(0L).orElseThrow(RuntimeException::new);
        return (end.createdAt.getTime() - start.createdAt.getTime()) * 1.0 / 1000 / entity.height;
    }

    @Override
    public Page<Block.Transaction> getTransactionListByAddress(String address, Pageable pageable) {
        return transactionDao.findByFromOrTo(address, address,pageable).map(x ->
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
                        .blockHeight(headerDao.findByBlockHash(x.blockHash).orElseThrow(RuntimeException::new).height)
                        .to(x.to)
                        .fee(x.fee)
                        .createdAt(x.createdAt)
                        .gasLimit(x.gasLimit)
                        .position(x.position)
                        .size(x.size)
                        .build());
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

    @Override
    public String getVersion(){
        return  "v1.0.0";
    }

    @Override
    public List<MazeProfit> getFarmnaseMazeProfitList(int chainId,String accountAddress,int type,String assetAddress) {
        List<MazeProfit> list = new ArrayList<>();
        List<FarmBaseEntity> batchMintEntities;
        if(type == 2){
            batchMintEntities = farmBaseDao.findByAccountAddressAndTypeAndChainId(accountAddress, type, chainId);
        } else {
            batchMintEntities = farmBaseDao.findByAccountAddressAndTypeAndChainIdAndAssetAddress(accountAddress, type, chainId, assetAddress);
        }
        list = batchMintEntities.stream().map(mapper).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<MazeProfit> getAll() {
        return farmBaseDao.findAll().stream().map(mapper).collect(Collectors.toList());
    }

    private Function<FarmBaseEntity, MazeProfit> mapper = x -> MazeProfit.builder()
            .accountAddress(x.accountAddress)
            .age(x.age)
            .smazeAccount(x.smazeAccount)
            .assetAddress(x.assetAddress)
            .blockHeight(x.blockHeight)
            .mappingContractAddress(x.mappingContractAddress)
            .transcationHash(x.transcationHash)
            .chainId(Integer.toString(x.chainId))
            .type(x.type)
            .createdAt(x.createdAt)
            .id(x.FarmBaseId)
            .operation(x.operation)
            .startHeight(x.startHeight)
            .endHeight(x.endHeight)
            .build();

    @Override
    public MazeProfit getByMaxAge(String assetAddress, int type, String accountAddress) {
        Optional<Integer> o;
        List<FarmBaseEntity> list;
        if(type == 2){
            o = farmBaseDao.getMaxAgeBylpMaze(type, accountAddress);
        } else{
            o = farmBaseDao.getMaxAge(assetAddress, type, accountAddress);
        }


        if (!o.isPresent())
            return null;
        int age = o.get();
        if(type == 2){
            list = farmBaseDao.getByMaxAgeBylpMaze(type, accountAddress, age);
        }else{
            list = farmBaseDao.getByMaxAge(assetAddress, type, accountAddress, age);
        }

        FarmBaseEntity e = list.get(0);
        return mapper.apply(e);
    }
//    @Override
//    public List<MazeProfit> getlist() {
//        List<BatchMintEntity> list1 = batchMintDao.findAll();
//        for(int i = 0;i<list1.size();i++){
//            String a = Hex.toHexString(list1.get(i).accountAddress);
//            System.out.println("==============="+a);
//        }
//        List<MazeProfit> list = new ArrayList<>();
//        return list;
//    }
}
