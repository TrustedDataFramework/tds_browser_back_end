package org.wisdom.tds_browser.entity;

import lombok.NonNull;
import org.springframework.util.StringUtils;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.wisdom.tds_browser.data.Block;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Mapping {

    public static HeaderEntity getEntityFromHeader(Block block) {
        return HeaderEntity.builder()
                .blockHash(block.hash)
                .hashPrevBlock(block.hashPrev)
                .height(block.height)
                .size(block.size)
                .stateRoot(block.stateRoot)
                .transactionsRoot(block.transactionsRoot)
                .createdAt(block.createdAt)
                .extraData(block.extraData)
                .build();
    }

    public static TransactionEntity getEntityFromTransaction(Block block, @NonNull Block.Transaction tx) {
        return TransactionEntity.builder()
                .amount(tx.amount)
                .from(StringUtils.isEmpty(tx.from) ? "" : tx.from)
                .r(tx.r)
                .v(tx.v)
                .s(tx.s)
                .to(tx.to)
                .position(tx.position)
                .blockHash(block.hash)
                .createdAt(tx.createdAt)
                .raw(tx.raw)
                .nonce(tx.nonce)
                .txHash(tx.hash)
                .height(block.height)
                .gasPrice(tx.gasPrice)
                .gas(tx.gas)
                .type(tx.type)
                .build();
    }

    public static List<TransactionEntity> getEntitiesFromTransactions(Block block) {
        return block.body.stream().map(x -> Mapping.getEntityFromTransaction(block, x)).collect(Collectors.toList());
    }

    public static Block convertBlock(org.web3j.protocol.core.methods.response.EthBlock.Block block) {
        List<Block.Transaction> list = block.getTransactions().stream().map(Mapping::convertTransaction).collect(Collectors.toList());
        return Block.builder()
                .size(block.getSize().longValue())
                .hashPrev(block.getParentHash())
                .createdAt(new Date(block.getTimestamp().longValue() * 1000))
                .height(block.getNumber().longValue())
                .stateRoot(block.getStateRoot())
                .hash(block.getHash())
                .transactionsRoot(block.getTransactionsRoot())
                .extraData(block.getExtraData())
                .body(list)
                .build();
    }

    public static Block.Transaction convertTransaction(EthBlock.TransactionResult transactionResult) {
        EthBlock.TransactionObject result = (EthBlock.TransactionObject)transactionResult.get();
        int type;
        if (result.getTransactionIndex().intValue() == 0) {
            type = 0;
        } else if (result.getTo() == null) {
            type = 2;
        } else if (result.getInput() == null) {
            type = 3;
        } else {
            type = 1;
        }
        return Block.Transaction.builder()
                .type(type)
                .position(result.getTransactionIndex().intValue())
                .amount(result.getValue().longValue())
                .createdAt(result.getCreates() == null ? null :new Date(result.getCreates()))
                .from(result.getFrom())
                .hash(result.getHash())
                .nonce(result.getNonceRaw())
                .raw(result.getNonceRaw())
                .r(result.getR())
                .s(result.getS())
                .v(result.getV())
                .to(result.getTo())
                .gasPrice(result.getGasPriceRaw())
                .gas(result.getGasRaw())
                .input(result.getInput())
                .build();
    }

}
