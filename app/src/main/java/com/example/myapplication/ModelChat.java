package com.example.myapplication;

public class ModelChat {


    private String message ;
    private String type ;
    private String senderId ;

    public ModelChat() {
    }

    public ModelChat(String message, String type, String senderId) {
        this.message = message;
        this.type = type;
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getSenderId() {
        return senderId;
    }
}
