package org.wisdom.tds_browser.result;

public class APIResult<T> extends ResultSupport {
    public static int FAIL = 500;
    public static int SUCCESS = 200;
    protected T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <U> APIResult<U> newFailResult(int code, String message, U data) {
        APIResult<U> apiResult = new APIResult<U>();
        apiResult.setCode(code);
        apiResult.setMessage(message);
        apiResult.setData(data);
        return apiResult;
    }

    public static <U> APIResult<U> newFailResult(int code, String message) {
        APIResult<U> apiResult = new APIResult<U>();
        apiResult.setCode(code);
        apiResult.setMessage(message);
        return apiResult;
    }

    public static <U> APIResult<U> newFailed(String message) {
        APIResult<U> apiResult = new APIResult<U>();
        apiResult.setCode(FAIL);
        apiResult.setMessage(message);
        return apiResult;
    }

    public static <U> APIResult<U> newSuccess(U data){
        APIResult<U> apiResult = new APIResult<U>();
        apiResult.setData(data);
        apiResult.setCode(SUCCESS);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }
}