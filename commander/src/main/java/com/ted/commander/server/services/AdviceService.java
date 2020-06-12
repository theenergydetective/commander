/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.AdviceState;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.AdviceDAO;
import com.ted.commander.server.dao.AdviceRecipientDAO;
import com.ted.commander.server.dao.AdviceTriggerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service for handling Advice
 */
@Service
public class AdviceService {

    static final Logger LOGGER = LoggerFactory.getLogger(AdviceService.class);

    @Autowired
    AdviceDAO adviceDAO;

    @Autowired
    AdviceTriggerDAO adviceTriggerDAO;

    @Autowired
    AdviceRecipientDAO adviceRecipientDAO;

    @Autowired
    VirtualECCService virtualECCService;



    public Advice createAdvice(User commanderUser, Advice advice){

        LOGGER.info("Creating Advice: {} by {}", advice, commanderUser.getUsername());
        advice.setAdviceName("TED Advice");
        advice.setVirtualECCId(virtualECCService.findDefault(commanderUser.getUsername()).getId());
        advice.setState(AdviceState.NORMAL);
        advice = adviceDAO.insert(advice);
        AdviceRecipient adviceRecipient = new AdviceRecipient(advice.getId(), commanderUser.getId());
        saveRecipient(adviceRecipient);
        LOGGER.info("Creating {}", advice);
        return advice;
    }

    public void updateAdvice(Advice advice) {
        Advice originalAdvice = adviceDAO.findOne(advice.getId());
        originalAdvice.setAdviceName(advice.getAdviceName());
        originalAdvice.setVirtualECCId(advice.getVirtualECCId());
        LOGGER.info("Saving {}", advice);
        adviceDAO.save(originalAdvice);
    }

    public List<Advice> findForLocation(Long locationId) {
        VirtualECC location = virtualECCService.findOneFromCache(locationId);
        List<Advice> adviceList = adviceDAO.findByVirtualECC(locationId);
        for (Advice advice: adviceList){
            advice.setLocationName(location.getName());
            advice.setTriggerList(adviceTriggerDAO.findPostAdvice(advice.getId()));
            if (advice.getTriggerList() != null && advice.getTriggerList().size() > 0) {
                //Don't bother loading if no applicable triggers are here.
                advice.setAdviceRecipientList(adviceRecipientDAO.findByAdvice(advice.getId()));
            }
        }
        return adviceList;
    }

    public void deleteAdvice(Advice advice) {
        LOGGER.info("Deleting {}", advice);
        adviceDAO.delete(advice.getId());
        adviceTriggerDAO.deleteByAdvice(advice.getId());
        adviceRecipientDAO.deleteByAdvice(advice.getId());
    }

    public Advice findById(Long adviceId) {
        Advice advice = adviceDAO.findOne(adviceId);
        advice.setAdviceRecipientList(adviceRecipientDAO.findByAdvice(advice.getId()));
        advice.setTriggerList(adviceTriggerDAO.findByAdvice(advice.getId()));
        advice.setLocationName(virtualECCService.findOneFromCache(advice.getVirtualECCId()).getName());
        return advice;
    }

    public List<Advice> findForAccount(Long accountId, boolean loadDetails) {
        List<Advice> adviceList = adviceDAO.findByAccount(accountId);
        for (Advice advice: adviceList) {
            if (loadDetails) {
                advice.setAdviceRecipientList(adviceRecipientDAO.findByAdvice(advice.getId()));
                advice.setTriggerList(adviceTriggerDAO.findByAdvice(advice.getId()));
            }
            advice.setLocationName(virtualECCService.findOneFromCache(advice.getVirtualECCId()).getName());
        }
        return adviceList;
    }

    public AdviceRecipient saveRecipient(AdviceRecipient adviceRecipient) {
        AdviceRecipient existingRecipient = adviceRecipientDAO.findOne(adviceRecipient.getAdviceId(), adviceRecipient.getUserId());
        if (existingRecipient == null){
            LOGGER.info("Creating {}", adviceRecipient);
            adviceRecipientDAO.insert(adviceRecipient);
            return adviceRecipient;
        } else {
            LOGGER.info("Saving {}", adviceRecipient);
            existingRecipient.setSendEmail(adviceRecipient.isSendEmail());
            existingRecipient.setSendPush(adviceRecipient.isSendPush());
            adviceRecipientDAO.save(existingRecipient);
            return existingRecipient;
        }
    }

    public void deleteAdviceRecipient(AdviceRecipient adviceRecipient) {
        LOGGER.info("Deleting {}", adviceRecipient);
        adviceRecipientDAO.deleteOne(adviceRecipient.getAdviceId(), adviceRecipient.getUserId());
    }

    public AdviceTrigger saveAdviceTrigger(AdviceTrigger adviceTrigger) {
        AdviceTrigger existingTrigger = adviceTriggerDAO.findOne(adviceTrigger.getId());
        if (existingTrigger == null){
            LOGGER.info("Creating {}", adviceTrigger);
            adviceTriggerDAO.insert(adviceTrigger);
            return adviceTrigger;
        } else {
            LOGGER.info("Saving {}", adviceTrigger);
            adviceTriggerDAO.save(adviceTrigger);
            return adviceTrigger;
        }
    }

    public void deleteAdviceTrigger(AdviceTrigger adviceTrigger) {
        LOGGER.info("Deleting {}",adviceTrigger);
        adviceTriggerDAO.deleteOne(adviceTrigger.getId());
    }

    public List<Advice> findCommanderAdvice() {
        List<Advice> adviceList = adviceDAO.findByNoPost();
        for (Advice advice: adviceList){
            advice.setTriggerList(adviceTriggerDAO.findNoPostAdvice(advice.getId()));
            advice.setAdviceRecipientList(adviceRecipientDAO.findByAdvice(advice.getId()));
        }
        return adviceList;
    }

    //TED-299
    public void deleteByVirtualECCId(Long virtualECCId) {
        List<Advice> adviceList =  findForLocation(virtualECCId);
        for (Advice advice: adviceList){
            deleteAdvice(advice);
        }
    }
}
