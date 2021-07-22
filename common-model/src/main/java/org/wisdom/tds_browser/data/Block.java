package org.wisdom.tds_browser.data;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class Block implements Serializable {

    public long size;

    public String hashPrev;

    public Date createdAt;

    public long height;

    public String stateRoot;

    public String hash;

    public String transactionsRoot;

    public String extraData;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public List<Transaction> body;


    @AllArgsConstructor
    @Builder
    public static class Transaction implements Serializable {
        public String hash;
        public String nonce;
        public String from;
        public long amount;
        public String raw;
        public String to;
        public Date createdAt;
        public int position;
        public long v;
        public String r;
        public String s;
        public int type;
        public String gasPrice;
        public String gas;
        public String input;


        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }
    }

}
