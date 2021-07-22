package org.wisdom.tds_browser.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.wisdom.tds_browser.dao.*;
import org.wisdom.tds_browser.data.Block;
import org.wisdom.tds_browser.entity.*;
import org.wisdom.tds_browser.tool.NodeTool;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
                    NodeTool nodeTool) {
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
            if (entity == null) {
                entity = SyncHeightEntity.builder()
                        .height(0L)
                        .syncName("block_height")
                        .build();
                height = 0L;
            } else {
                height = entity.height;
            }

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
        if (block.body != null && block.body.size() != 0) {
            block.body.forEach(x -> {
                if (x.type == 2) {
                    contractDao.save(ContractEntity.builder()
                            .address(x.from)
                            .binary(x.input.getBytes())
                            .from(x.from)
                            .to(x.to)
                            .amount(x.amount)
                            .height(block.height)
                            .txHash(x.hash)
                            .createdAt(x.createdAt)
                            .build());
                }
            });
        }
    }
}
