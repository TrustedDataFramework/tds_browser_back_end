package org.wisdom.tds_browser.data;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
public class Block implements Serializable {

    public long version;

    public long size;

    public String hashPrev;

    public Date createdAt;

    public long height;

    public String stateRoot;

    public String hash;

    public String transactionsRoot;

    public String payload;



    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public List<Transaction> body;

    public List<String> rawData;

    @AllArgsConstructor
    public static class Transaction implements Serializable {
        public String hash;
        public int version;
        public int type;
        public long nonce;
        public String from;
        public long gasLimit;
        public long gasPrice;
        public long amount;
        public String payload;
        public String to;
        public String signature;
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
