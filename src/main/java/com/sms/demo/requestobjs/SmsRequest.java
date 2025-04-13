package com.sms.demo.requestobjs;

import java.util.List;

public class SmsRequest {
    private List<String> recipient;
    private String sender;
    private String message;
    private boolean isSchedule;
    private String scheduleDate;

    // Constructor
    public SmsRequest(List<String> recipient, String sender, String message, boolean isSchedule, String scheduleDate) {
        this.recipient = recipient;
        this.sender = sender;
        this.message = message;
        this.isSchedule = isSchedule;
        this.scheduleDate = scheduleDate;
    }

    // Getters and Setters
    public List<String> getRecipient() {
        return recipient;
    }

    public void setRecipient(List<String> recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSchedule() {
        return isSchedule;
    }

    public void setSchedule(boolean isSchedule) {
        this.isSchedule = isSchedule;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    @Override
    public String toString() {
        return "SmsRequest{" +
                "recipient=" + recipient +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", isSchedule=" + isSchedule +
                ", scheduleDate='" + scheduleDate + '\'' +
                '}';
    }
}

