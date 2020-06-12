package com.ted.commander.common.model;

import com.ted.commander.common.enums.AdviceTriggerState;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.SendAtMostType;
import com.ted.commander.common.enums.TriggerType;

import java.io.Serializable;

/**
 * Created by pete on 5/20/2016.
 */
public class AdviceTrigger implements Serializable {
    Long id = null;
    Long adviceId = null;
    TriggerType triggerType = TriggerType.RATE_CHANGE;
    int startTime = 0;
    int endTime = 0;

    SendAtMostType sendAtMost = SendAtMostType.DAILY;
    int delayMinutes = 0;
    int minutesBefore = 5;

    double amount = 0.0;
    boolean allMTUs = true;
    boolean offPeakApplicable = true;
    boolean peakApplicable = true;
    boolean midPeakApplicable = true;
    boolean superPeakApplicable = true;

    long lastSent = 0l;
    AdviceTriggerState triggerState = AdviceTriggerState.NORMAL;

    public AdviceTrigger() {
    }

    public AdviceTrigger(Long adviceId) {
        this.adviceId = adviceId;
    }

    HistoryType sinceStart = HistoryType.BILLING_CYCLE;

    public Long getId() {
        return id;
    }

    public int getMinutesBefore() {
        return minutesBefore;
    }

    public void setMinutesBefore(int minutesBefore) {
        this.minutesBefore = minutesBefore;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdviceId() {
        return adviceId;
    }

    public void setAdviceId(Long adviceId) {
        this.adviceId = adviceId;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public SendAtMostType getSendAtMost() {
        return sendAtMost;
    }

    public void setSendAtMost(SendAtMostType sendAtMost) {
        this.sendAtMost = sendAtMost;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isAllMTUs() {
        return allMTUs;
    }

    public void setAllMTUs(boolean allMTUs) {
        this.allMTUs = allMTUs;
    }

    public boolean isOffPeakApplicable() {
        return offPeakApplicable;
    }

    public void setOffPeakApplicable(boolean offPeakApplicable) {
        this.offPeakApplicable = offPeakApplicable;
    }

    public boolean isPeakApplicable() {
        return peakApplicable;
    }

    public void setPeakApplicable(boolean peakApplicable) {
        this.peakApplicable = peakApplicable;
    }

    public boolean isMidPeakApplicable() {
        return midPeakApplicable;
    }

    public void setMidPeakApplicable(boolean midPeakApplicable) {
        this.midPeakApplicable = midPeakApplicable;
    }

    public boolean isSuperPeakApplicable() {
        return superPeakApplicable;
    }

    public void setSuperPeakApplicable(boolean superPeakApplicable) {
        this.superPeakApplicable = superPeakApplicable;
    }

    public HistoryType getSinceStart() {
        return sinceStart;
    }

    public void setSinceStart(HistoryType sinceStart) {
        this.sinceStart = sinceStart;
    }

    public long getLastSent() {
        return lastSent;
    }

    public void setLastSent(long lastSent) {
        this.lastSent = lastSent;
    }

    public AdviceTriggerState getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(AdviceTriggerState triggerState) {
        this.triggerState = triggerState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdviceTrigger that = (AdviceTrigger) o;

        if (startTime != that.startTime) return false;
        if (endTime != that.endTime) return false;
        if (delayMinutes != that.delayMinutes) return false;
        if (minutesBefore != that.minutesBefore) return false;
        if (Double.compare(that.amount, amount) != 0) return false;
        if (allMTUs != that.allMTUs) return false;
        if (offPeakApplicable != that.offPeakApplicable) return false;
        if (peakApplicable != that.peakApplicable) return false;
        if (midPeakApplicable != that.midPeakApplicable) return false;
        if (superPeakApplicable != that.superPeakApplicable) return false;
        if (lastSent != that.lastSent) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (adviceId != null ? !adviceId.equals(that.adviceId) : that.adviceId != null) return false;
        if (triggerType != that.triggerType) return false;
        if (sendAtMost != that.sendAtMost) return false;
        if (triggerState != that.triggerState) return false;
        return sinceStart == that.sinceStart;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (adviceId != null ? adviceId.hashCode() : 0);
        result = 31 * result + (triggerType != null ? triggerType.hashCode() : 0);
        result = 31 * result + startTime;
        result = 31 * result + endTime;
        result = 31 * result + (sendAtMost != null ? sendAtMost.hashCode() : 0);
        result = 31 * result + delayMinutes;
        result = 31 * result + minutesBefore;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (allMTUs ? 1 : 0);
        result = 31 * result + (offPeakApplicable ? 1 : 0);
        result = 31 * result + (peakApplicable ? 1 : 0);
        result = 31 * result + (midPeakApplicable ? 1 : 0);
        result = 31 * result + (superPeakApplicable ? 1 : 0);
        result = 31 * result + (int) (lastSent ^ (lastSent >>> 32));
        result = 31 * result + (triggerState != null ? triggerState.hashCode() : 0);
        result = 31 * result + (sinceStart != null ? sinceStart.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AdviceTrigger{" +
                "id=" + id +
                ", adviceId=" + adviceId +
                ", triggerType=" + triggerType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", sendAtMost=" + sendAtMost +
                ", delayMinutes=" + delayMinutes +
                ", minutesBefore=" + minutesBefore +
                ", amount=" + amount +
                ", allMTUs=" + allMTUs +
                ", offPeakApplicable=" + offPeakApplicable +
                ", peakApplicable=" + peakApplicable +
                ", midPeakApplicable=" + midPeakApplicable +
                ", superPeakApplicable=" + superPeakApplicable +
                ", lastSent=" + lastSent +
                ", triggerState=" + triggerState +
                ", sinceStart=" + sinceStart +
                '}';
    }
}
