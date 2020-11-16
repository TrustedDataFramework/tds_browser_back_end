package org.wisdom.tds_browser.bean;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class Block implements Serializable {

    public long version;

    public long size;

    @JsonProperty("hash_prev")
    public String hashPrev;

    @JsonProperty("created_at")
    public Date createdAt;

    public long height;

    @JsonProperty("state_root")
    public String stateRoot;

    public String hash;

    @JsonProperty("transaction_root")
    public String transactionsRoot;

    public String payload;

    @JsonProperty("miner_address")
    public String minerAddress;

    @JsonProperty("all_fee")
    public long allFee;



    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public List<org.wisdom.tds_browser.bean.Block.Transaction> body;

    @AllArgsConstructor
    @Builder
    public static class Transaction implements Serializable {
        public String hash;
        public int version;
        public int type;
        public long nonce;
        public String from;
        @JsonProperty("gas_limit")
        public long gasLimit;
        @JsonProperty("gas_price")
        public long gasPrice;
        public long amount;
        public String payload;
        public String to;
        public String signature;
        @JsonProperty("created_at")
        public Date createdAt;
        public long fee;
        public long size;
        public int position;

        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }
    }

}
