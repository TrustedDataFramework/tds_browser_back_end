package org.wisdom.tds_browser.timer;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.tdf.common.types.Uint256;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.tdf.sunflower.state.Address;
import org.tdf.sunflower.types.Transaction;
import org.tdf.sunflower.vm.abi.ContractABI;
import org.tdf.sunflower.vm.abi.ContractCallPayload;
import org.tdf.sunflower.vm.abi.ContractDeployPayload;
import org.wisdom.tds_browser.dao.*;
import org.wisdom.tds_browser.data.Block;
import org.wisdom.tds_browser.entity.*;
import org.wisdom.tds_browser.tool.NodeTool;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j(topic = "sync-block")
@Component
public class BlockJob {

    private final Lock taskLock = new ReentrantLock();

    private final TransactionDao transactionDao;

    public final static int differHeight = 0;

    public final static int defaultSyncSize = 500;

    private final HeaderDao headerDao;

    private final NodeTool nodeTool;

    private final SyncHeightDao syncHeightDao;

    private final ContractDao contractDao;

    private final FarmBaseDao farmBaseDao;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String contractAddress = "9195d579008279df4b0404174d6d9e916739e431";

    static {
        String addr = System.getenv("FARMBASE_CONTRACT_ADDRESS").toLowerCase();
        if ( addr != null && !addr.isEmpty()) {
            contractAddress = addr;
        }
    }

    @PostConstruct
    public void init() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


        executorService.scheduleWithFixedDelay(
                this::getBlocks, 0,
                1, TimeUnit.SECONDS);
    }

    public BlockJob(SyncHeightDao syncHeightDao,
                    HeaderDao headerDao,
                    TransactionDao transactionDao,
                    ContractDao contractDao,
                    NodeTool nodeTool,
                    FarmBaseDao farmBaseDao) {
        this.syncHeightDao = syncHeightDao;
        this.headerDao = headerDao;
        this.transactionDao = transactionDao;
        this.nodeTool = nodeTool;
        this.contractDao = contractDao;
        this.farmBaseDao = farmBaseDao;
    }

    private void getBlocks() {
        long nodeHeight;
        long height;
        SyncHeightEntity entity;
        try {
            nodeHeight = nodeTool.getNodeHeight();
            entity = syncHeightDao.findBySyncName("block_height");
            height = entity.height;
            if (height + differHeight >= nodeHeight) {
                return;
            }
            log.info("同步区块=======>高度：" + height + "  节点高度：" + nodeHeight);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (taskLock.tryLock()) {
            try {
                long blockSize = Math.min(nodeHeight - height, defaultSyncSize);
                for (int i = 0; i <= blockSize; i++) {
                    Block block = nodeTool.getBlocks(height + i);
                    if (block.height < height) {
                        continue;
                    }
                    Optional<HeaderEntity> op = headerDao.findByHeight(block.height);
                    if (!op.isPresent()) {
                        writeBlock(block);
                    }
                }
                entity.height = nodeTool.getBlocks(height + blockSize).height;
                syncHeightDao.save(entity);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                taskLock.unlock();
            }
        }
    }

    // bsc 0x56

    // map (链id -> TDOS合约地址)

    // 2. ID 链id, 资产地址, 矿池类型(0,1,2), 用户地址  矿工映射
    // 3. 增发记录 ID, 区块高度, 事务哈希, smaze 数量
    // 4. 领取记录 ID, 区块高度, 事务哈希, smaze 数量


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeBlock(Block block) {
        HeaderEntity entity = Mapping.getEntityFromHeader(block);
        transactionDao.saveAll(Mapping.getEntitiesFromTransactions(block));
        headerDao.save(entity);
        if (block.rawData != null && block.rawData.size() != 0) {
            block.rawData.forEach(x -> {
                try {
                    Transaction transaction = OBJECT_MAPPER.readValue(x, Transaction.class);
                    if (transaction.getType() == Transaction.Type.CONTRACT_DEPLOY.code) {
                        String contractAddress = transaction.createContractAddress().toHex();
                        ContractDeployPayload payload = RLPCodec.decode(transaction.getPayload().getBytes(), ContractDeployPayload.class);
                        byte[] binary = payload.getBinary();
                        List<ContractABI> abi = payload.getContractABIs();
                        List<ContractABI.ContractABIJson> list = abi.stream().map(ContractABI::toJSON).collect(Collectors.toList());
                        contractDao.save(ContractEntity.builder()
                                .address(contractAddress)
                                .abi(JSON.toJSONString(list).getBytes())
                                .binary(binary)
                                .from(Address.fromPublicKey(transaction.getFrom()).toHex())
                                .to(transaction.getTo().toHex())
                                .amount(transaction.getAmount().longValue())
                                .height(block.height)
                                .txHash(transaction.getHash().toHex())
                                .createdAt(new Date(transaction.getCreatedAt() * 1000))
                                .build());

                    }
                    if(block.height == 30012){
                        log.info("==============================");
                    }
                    if(transaction.getType() == 3 && transaction.getTo().toHex().equals(contractAddress)/* && transaction.to in ["maze 计算合约的地址"] */) {
                         ContractCallPayload callPayload = RLPCodec.decode(transaction.getPayload().getBytes(), ContractCallPayload.class);

                        int age = 0;

                        // 1. batchMint 2. burn
                        String method = callPayload.getMethod();
                        RLPList list = callPayload.getParameters().getLi().asRLPList();

                        if(method.equals("batchMint") || method.equals("batchBurn")){
                            boolean batchMint = method.equals("batchMint");
                            // batchMint
                            int chainId =  list.get(0).asInt();
                            HexBytes[] assets = RLPElement.fromEncoded(list.get(1).asBytes()).as(HexBytes[].class);

                            int[] mtypes = RLPElement.fromEncoded(list.get(2).asBytes()).as(int[].class);
                            HexBytes[] users = RLPElement.fromEncoded(list.get(3).asBytes()).as(HexBytes[].class);
                            Uint256[] amounts = RLPElement.fromEncoded(list.get(4).asBytes()).as(Uint256[].class);
                            age = list.get(5).asInt();


                            long startHeight = (!batchMint || list.size() < 7) ? 0 : list.get(6).asLong();
                            long endHeight = (!batchMint || list.size() < 8) ? 0 : list.get(7).asLong();

                            for (int i = 0; i < assets.length; i++) {
                                Keccak.Digest256 digest = new Keccak.Digest256();
                                digest.update((byte)56);
                                digest.update(assets[i].getBytes(), 0, assets[i].getBytes().length);
                                digest.update((byte) mtypes[i]);
                                digest.update(users[i].getBytes(), 0, users[i].getBytes().length);

                                byte[] hash = digest.digest();
                                // addr 矿工映射合约地址
                                byte[] addr = Arrays.copyOfRange(hash, hash.length - 20, hash.length);
                                long id;
                                if(farmBaseDao.getMaxId().isPresent()){
                                    id = farmBaseDao.getMaxId().get() + 1;
                                }else{
                                    id = 1;
                                }

                                if(!farmBaseDao.findByTranscationHashAndAssetAddressAndAccountAddressAndAgeAndTypeAndBlockHeightAndOperation(transaction.getHash().toHex()
                                ,assets[i].toHex() ,users[i].toHex(),age,mtypes[i],block.height,"Mint").isPresent()) {

                                    farmBaseDao.save(
                                            FarmBaseEntity
                                                    .builder()
                                                    .FarmBaseId(id)
                                                    .blockHeight(block.height)
                                                    .transcationHash(transaction.getHash().toHex())
                                                    .smazeAccount(new BigDecimal(amounts[i].value()))
                                                    .age(age)
                                                    .chainId(chainId)
                                                    .assetAddress(assets[i].toHex())
                                                    .type(mtypes[i])
                                                    .accountAddress(users[i].toHex())
                                                    .mappingContractAddress(HexBytes.encode(addr))
                                                    .createdAt(new Date(transaction.getCreatedAt() * 1000))
                                                    .operation(batchMint ? "Mint" : "Burn")
                                                    .startHeight(startHeight)
                                                    .endHeight(endHeight)
                                                    .build()
                                    );
                                }
                            }
                        }else if(method.equals("burn")) {

                            int chainId = list.get(0).asInt();
                            HexBytes asset = list.get(1).as(HexBytes.class);
                            int mtype = list.get(2).asInt();
                            HexBytes user = list.get(3).as(HexBytes.class);
                            Uint256 amount = list.get(4).as(Uint256.class);
                            age = list.get(5).asInt();


                            Keccak.Digest256 digestBurn = new Keccak.Digest256();
                            digestBurn.update((byte) 56);
                            digestBurn.update(asset.getBytes(), 0, asset.getBytes().length);
                            digestBurn.update((byte) mtype);
                            digestBurn.update(user.getBytes(), 0, user.getBytes().length);

                            byte[] hashBurn = digestBurn.digest();
                            // addr 矿工映射合约地址
                            byte[] addrBurn = Arrays.copyOfRange(hashBurn, hashBurn.length - 20, hashBurn.length);
                            long id;
                            if (farmBaseDao.getMaxId().isPresent()) {
                                id = farmBaseDao.getMaxId().get() + 1;
                            } else {
                                id = 1;
                            }
                            if (!farmBaseDao.findByTranscationHashAndAssetAddressAndAccountAddressAndAgeAndTypeAndBlockHeightAndOperation(transaction.getHash().toHex()
                                    , asset.toHex(), user.toHex(), age,mtype, block.height, "Burn").isPresent()) {

                                farmBaseDao.save(FarmBaseEntity.builder()
                                        .FarmBaseId(id)
                                        .blockHeight(block.height)
                                        .transcationHash(transaction.getHash().toHex())
                                        .smazeAccount(new BigDecimal(amount.value()))
                                        .age(age)
                                        .chainId(chainId)
                                        .assetAddress(asset.toHex())
                                        .type(mtype)
                                        .accountAddress(user.toHex())
                                        .mappingContractAddress(HexBytes.encode(addrBurn))
                                        .createdAt(new Date(transaction.getCreatedAt() * 1000))
                                        .operation("Burn")
                                        .startHeight(0)
                                        .endHeight(0)
                                        .build());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
