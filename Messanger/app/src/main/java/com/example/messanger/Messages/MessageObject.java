package com.example.messanger.Messages;

public class MessageObject {
    String message, sender, messageID, chatID;

    public MessageObject(String message, String sender, String messageID, String ChatID) {
        this.message = message;
        this.sender = sender;
        this.messageID = messageID;
        this.chatID = ChatID;
    }

    public String getChatID(){return chatID;};

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageID() {
        return messageID;
    }
}
