package com.ted.commander.common.model;

import com.ted.commander.common.enums.AdviceState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pete on 5/20/2016.
 */
public class Advice implements Serializable {
    Long id;
    String adviceName;
    Long virtualECCId;
    Long accountId;
    AdviceState state = AdviceState.NORMAL;

    String locationName;
    List<AdviceRecipient> adviceRecipientList = new ArrayList<>();
    List<AdviceTrigger> triggerList = new ArrayList<>();

    public Advice(Long id) {
        this.adviceName = "New Advice";
        this.accountId = id;
        this.virtualECCId = 0l;
    }

    public Advice() {
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdviceName() {
        return adviceName;
    }

    public void setAdviceName(String adviceName) {
        this.adviceName = adviceName;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public AdviceState getState() {
        return state;
    }

    public void setState(AdviceState state) {
        this.state = state;
    }

    public List<AdviceRecipient> getAdviceRecipientList() {
        return adviceRecipientList;
    }

    public void setAdviceRecipientList(List<AdviceRecipient> adviceRecipientList) {
        this.adviceRecipientList = adviceRecipientList;
    }

    public List<AdviceTrigger> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<AdviceTrigger> triggerList) {
        this.triggerList = triggerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Advice advice = (Advice) o;

        if (id != null ? !id.equals(advice.id) : advice.id != null) return false;
        if (adviceName != null ? !adviceName.equals(advice.adviceName) : advice.adviceName != null) return false;
        if (virtualECCId != null ? !virtualECCId.equals(advice.virtualECCId) : advice.virtualECCId != null)
            return false;
        if (accountId != null ? !accountId.equals(advice.accountId) : advice.accountId != null) return false;
        if (state != advice.state) return false;
        if (adviceRecipientList != null ? !adviceRecipientList.equals(advice.adviceRecipientList) : advice.adviceRecipientList != null)
            return false;
        return triggerList != null ? triggerList.equals(advice.triggerList) : advice.triggerList == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (adviceName != null ? adviceName.hashCode() : 0);
        result = 31 * result + (virtualECCId != null ? virtualECCId.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (adviceRecipientList != null ? adviceRecipientList.hashCode() : 0);
        result = 31 * result + (triggerList != null ? triggerList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Advice{" +
                "id=" + id +
                ", adviceName='" + adviceName + '\'' +
                ", virtualECCId=" + virtualECCId +
                ", accountId=" + accountId +
                ", state=" + state +
                ", adviceRecipientList=" + adviceRecipientList +
                ", triggerList=" + triggerList +
                '}';
    }


}
