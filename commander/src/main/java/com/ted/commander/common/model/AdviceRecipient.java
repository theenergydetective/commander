package com.ted.commander.common.model;

import java.io.Serializable;

/**
 * Created by pete on 5/20/2016.
 */
public class AdviceRecipient implements Serializable {
    Long adviceId;
    Long userId;
    boolean sendEmail = true;
    boolean sendPush = true;
    String displayName;
    String email;

    public AdviceRecipient() {
    }

    public AdviceRecipient(Long adviceId, Long userId) {
        this.adviceId = adviceId;
        this.userId = userId;
    }

    public Long getAdviceId() {
        return adviceId;
    }

    public void setAdviceId(Long adviceId) {
        this.adviceId = adviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isSendPush() {
        return sendPush;
    }

    public void setSendPush(boolean sendPush) {
        this.sendPush = sendPush;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "AdviceRecipient{" +
                "adviceId=" + adviceId +
                ", userId=" + userId +
                ", sendEmail=" + sendEmail +
                ", sendPush=" + sendPush +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdviceRecipient that = (AdviceRecipient) o;

        if (sendEmail != that.sendEmail) return false;
        if (sendPush != that.sendPush) return false;
        if (adviceId != null ? !adviceId.equals(that.adviceId) : that.adviceId != null) return false;
        return userId != null ? userId.equals(that.userId) : that.userId == null;

    }

    @Override
    public int hashCode() {
        int result = adviceId != null ? adviceId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (sendEmail ? 1 : 0);
        result = 31 * result + (sendPush ? 1 : 0);
        return result;
    }
}
