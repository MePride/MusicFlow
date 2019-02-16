package com.mepride.musicflow.beans;

public class MessageBean {

    private String fromUserNickName;
    private String fromUserAvatarUrl;
    private String toUserAvatarUrl;
    private String Message;

    public MessageBean(String fromUserNickName,String fromUserAvatarUrl,String toUserAvatarUrl,String Message){
        this.fromUserAvatarUrl = fromUserAvatarUrl;
        this.fromUserNickName = fromUserNickName;
        this.Message = Message;
        this.toUserAvatarUrl = toUserAvatarUrl;
    }

    public String getFromUserAvatarUrl() {
        return fromUserAvatarUrl;
    }

    public String getFromUserNickName() {
        return fromUserNickName;
    }

    public String getMessage() {
        return Message;
    }

    public String getToUserAvatarUrl() {
        return toUserAvatarUrl;
    }

    public void setFromUserAvatarUrl(String fromUserAvatarUrl) {
        this.fromUserAvatarUrl = fromUserAvatarUrl;
    }

    public void setFromUserNickName(String fromUserNickName) {
        this.fromUserNickName = fromUserNickName;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public void setToUserAvatarUrl(String toUserAvatarUrl) {
        this.toUserAvatarUrl = toUserAvatarUrl;
    }
}
