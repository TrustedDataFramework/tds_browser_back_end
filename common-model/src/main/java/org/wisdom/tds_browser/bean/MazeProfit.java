package org.wisdom.tds_browser.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class MazeProfit implements Serializable {
    public long id;

    public long blockHeight;

    public String transcationHash;

    public Number smazeAccount;

    public long age;

    public Date createdAt;

    public String chainId;

    public String assetAddress;

    public int type;

    public String accountAddress;

    public String mappingContractAddress;

    public String operation;

    public long startHeight;

    public long endHeight;
}
