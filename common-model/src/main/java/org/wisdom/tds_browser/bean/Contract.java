package org.wisdom.tds_browser.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

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


}
