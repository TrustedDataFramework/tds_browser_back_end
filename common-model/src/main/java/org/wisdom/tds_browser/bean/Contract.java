package org.wisdom.tds_browser.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class Contract  implements Serializable {

    public String address;

    public long height;

    public String from;

    public String to;

    public long amount;

    @JsonProperty("tx_hash")
    public String txHash;

    public long fee;

    @JsonProperty("created_at")
    public Date createdAt;


}
