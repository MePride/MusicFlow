package com.mepride.musicflow.beans;

public class Bean {
    public String code;
    public String msg;
    public data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Bean.data getData() {
        return data;
    }

    public void setData(Bean.data data) {
        this.data = data;
    }

    public class data {
        public String token;
        public String accountid;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAccountid() {
            return accountid;
        }

        public void setAccountid(String accountid) {
            this.accountid = accountid;
        }
    }
}
