package com.company.framework.globalresponse.gracefulresponse.defaults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatus;

/**
 * copy from DefaultResponseImplStyle1
 *
 * @author qinyujie
 */
public class DataNullResponseImplStyle1 implements Response {

    // private Object data = Collections.emptyMap();
    // 框架中DefaultResponseImplStyle1 data默认为空对象，但实际业务中查不到数据就会返回data=null，这里整个保留了null的意义
    private Object data = null;

    // private ResponseStatus status;
    // 框架中DefaultResponseImplStyle1 status默认为空对象，会导致json反序列化报NPE
    private ResponseStatus status = new DefaultResponseStatus();// 与ResponseStatusFactory.newInstance实现类保持一致

    @Override
    public void setStatus(ResponseStatus statusLine) {
        this.status = statusLine;
    }

    @Override
    @JsonIgnore
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public void setPayload(Object payload) {
        this.data = payload;
    }

    @Override
    @JsonIgnore
    public Object getPayload() {
        return this.data;
    }

    public String getCode() {
        return this.status.getCode();
    }

    public void setCode(String code) {
        this.status.setCode(code);
    }

    public String getMsg() {
        return this.status.getMsg();
    }

    public void setMsg(String msg) {
        this.status.setMsg(msg);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
