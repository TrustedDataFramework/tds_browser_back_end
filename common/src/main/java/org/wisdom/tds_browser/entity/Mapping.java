package org.wisdom.tds_browser.entity;

import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.StringUtils;
import org.tdf.common.util.HexBytes;
import org.tdf.sunflower.state.Address;
import org.wisdom.tds_browser.data.Block;

import java.util.List;
import java.util.stream.Collectors;

public class Mapping {

    public static HeaderEntity getEntityFromHeader(Block block) {
        return HeaderEntity.builder()
                .blockHash(block.hash)
                .hashPrevBlock(block.hashPrev)
                .height(block.height)
                .size(block.size)
                .payload(Hex.decode(block.payload))
                .version(block.version)
                .stateRoot(block.stateRoot)
                .transactionsRoot(block.transactionsRoot)
                .createdAt(block.createdAt)
                .build();
    }

    public static TransactionEntity getEntityFromTransaction(Block block, @NonNull Block.Transaction tx) {
        return TransactionEntity.builder()
                .amount(tx.amount)
                .from(StringUtils.isEmpty(tx.from) ? "" : Address.fromPublicKey(HexBytes.decode(tx.from)).toHex())
                .gasPrice(tx.gasPrice)
                .gasLimit(tx.gasLimit)
                .signature(tx.signature)
                .to(tx.to)
                .position(tx.position)
                .blockHash(block.hash)
                .createdAt(tx.createdAt)
                .payload(Hex.decode(tx.payload))
                .nonce(tx.nonce)
                .fee(tx.fee)
                .txHash(tx.hash)
                .size(tx.size)
                .type(tx.type)
                .version(tx.version)
                .build();
    }

    public static List<TransactionEntity> getEntitiesFromTransactions(Block block) {
        return block.body.stream().map(x -> Mapping.getEntityFromTransaction(block, x)).collect(Collectors.toList());
    }

}
