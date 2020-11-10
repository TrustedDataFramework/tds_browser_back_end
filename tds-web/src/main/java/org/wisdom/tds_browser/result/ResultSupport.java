package org.wisdom.tds_browser.result;


import java.io.Serializable;

public class ResultSupport implements Serializable {
    private String message;
    private int Code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }
}