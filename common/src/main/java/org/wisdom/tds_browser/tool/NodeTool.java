package org.wisdom.tds_browser.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import org.wisdom.tds_browser.data.Block;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NodeTool {

    public String[] peers;

    private int index = 0;

    public String nodeUrl;

    public NodeTool(@Value("${dandelion.peers}")
                            String peers) {
        this.peers = peers.replaceAll(" ", "").split(",");
        this.nodeUrl = this.peers[0];
    }

    @PostConstruct
    public void init() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(
                this::replaceNode, 0, 3600, TimeUnit.SECONDS);
    }

    private void replaceNode() {
        index++;
        this.nodeUrl = this.peers[index % (this.peers.length)];
        log.info("当前节点： " + nodeUrl);
    }

    public Block getBlocks(long height) {
        String body = HttpRequest.get(nodeUrl + "/rpc/block/" + height)
                .connectTimeout(5000)
                .readTimeout(5000)
                .body();
        JSONObject jo = (JSONObject) JSON.parseObject(body).get("data");
        Block block = JSON.parseObject(jo.toJSONString(), Block.class);
        if (block.body == null) {
            return block;
        }
        JSONArray raw = jo.getJSONArray("body");
        block.rawData = raw.toJavaList(String.class);
        for (int i = 0; i < block.body.size(); i++) {
            block.body.get(i).position = i;
        }
        return block;
    }

    public long getNodeHeight() {
        String body = HttpRequest.get(nodeUrl + "/rpc/header/-1").body();
        return JSON.parseObject(body).getJSONObject("data").getLong("height");
    }

    public String stat() {
        return HttpRequest.get(nodeUrl + "/rpc/stat")
                .connectTimeout(5000)
                .readTimeout(5000)
                .body();
    }

}
