package org.wisdom.tds_browser.timer;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.sunflower.state.Address;
import org.tdf.sunflower.types.Transaction;
import org.tdf.sunflower.vm.abi.ContractABI;
import org.tdf.sunflower.vm.abi.ContractDeployPayload;
import org.wisdom.tds_browser.dao.ContractDao;
import org.wisdom.tds_browser.dao.HeaderDao;
import org.wisdom.tds_browser.dao.SyncHeightDao;
import org.wisdom.tds_browser.dao.TransactionDao;
import org.wisdom.tds_browser.data.Block;
import org.wisdom.tds_browser.entity.ContractEntity;
import org.wisdom.tds_browser.entity.HeaderEntity;
import org.wisdom.tds_browser.entity.Mapping;
import org.wisdom.tds_browser.entity.SyncHeightEntity;
import org.wisdom.tds_browser.tool.NodeTool;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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

    public final static int differHeight = 3;

    public final static int defaultSyncSize = 50;

    private final HeaderDao headerDao;

    private final NodeTool nodeTool;

    private final SyncHeightDao syncHeightDao;

    private final ContractDao contractDao;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @PostConstruct
    public void init() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(
                this::getBlocks, 0,
                3, TimeUnit.SECONDS);
    }

    public BlockJob(SyncHeightDao syncHeightDao,
                    HeaderDao headerDao,
                    TransactionDao transactionDao,
                    ContractDao contractDao,
                    NodeTool nodeTool
    ) {
        this.syncHeightDao = syncHeightDao;
        this.headerDao = headerDao;
        this.transactionDao = transactionDao;
        this.nodeTool = nodeTool;
        this.contractDao = contractDao;
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
                    if (block.height <= height) {
                        continue;
                    }
                    System.out.println(block.toString());
                    writeBlock(block);
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeBlock(Block block) {
        HeaderEntity entity = Mapping.getEntityFromHeader(block);
        headerDao.save(entity);
        transactionDao.saveAll(Mapping.getEntitiesFromTransactions(block));
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
