/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.WeatherKey;
import com.ted.commander.server.util.CalendarUtils;
import com.ted.commander.server.util.XMLUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;

/**
 * Created by pete on 10/29/2014.
 */
@Service
public class VirtualECCService {

    final static Logger LOGGER = LoggerFactory.getLogger(VirtualECCService.class);

    @Autowired
    UserService userService;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    MTUDAO mtudao;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;


    @Autowired
    WeatherService weatherService;



    @Autowired
    AdviceDAO adviceDAO;

    @Autowired
    AdviceTriggerDAO adviceTriggerDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    AccountMemberDAO accountMemberDAO;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;
    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    HistoryHourDAO historyHourDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    HistoryMTUHourDAO historyMTUHourDAO;

    @Autowired
    HistoryMTUDayDAO historyMTUDayDAO;

    @Autowired
    HistoryMTUBillingCycleDAO historyMTUBillingCycleDAO;

    @Autowired
    AdviceRecipientDAO adviceRecipientDAO;

    public VirtualECC update(VirtualECC virtualECC){
        boolean createLocation = virtualECC.getId() == null || virtualECC.getId().equals(0L);
        virtualECC = virtualECCDAO.update(virtualECC);
        if (createLocation){
            LOGGER.info("Creating a 24 No Post Trigger for {}", virtualECC);
            Advice advice = new Advice();
            advice.setLocationName(virtualECC.getName());
            advice.setAdviceName("TED Advice");
            advice.setState(AdviceState.NORMAL);
            advice.setVirtualECCId(virtualECC.getId());
            advice.setAccountId(virtualECC.getAccountId());
            advice = adviceDAO.insert(advice);

            AdviceTrigger adviceTrigger = new AdviceTrigger();
            adviceTrigger.setAllMTUs(true);
            adviceTrigger.setTriggerType(TriggerType.COMMANDER_NO_POST);
            adviceTrigger.setDelayMinutes((24 * 60));
            adviceTrigger.setSendAtMost(SendAtMostType.DAILY);
            adviceTrigger.setAdviceId(advice.getId());
            adviceTriggerDAO.insert(adviceTrigger);


            List<AccountMember> accounts = accountMemberDAO.findByAccount(virtualECC.getAccountId());
            for (AccountMember m: accounts){
                if (m.getAccountRole().equals(AccountRole.OWNER)){
                    AdviceRecipient adviceRecipient = new AdviceRecipient();
                    adviceRecipient.setAdviceId(advice.getId());
                    adviceRecipient.setSendEmail(true);
                    adviceRecipient.setSendPush(true);
                    adviceRecipient.setUserId(m.getUserId());
                    adviceRecipient.setDisplayName(m.getUser().getFormattedName());
                    adviceRecipient.setEmail(m.getUser().getUsername());
                    adviceRecipientDAO.insert(adviceRecipient);
                }
            }






        }


        return virtualECC;
    }


    public VirtualECC parseFromECCXML(String description, Long accountId, Long energyPlanId, String xmlData) {
        VirtualECC virtualECC = new VirtualECC();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlData)));
            Element doc = document.getDocumentElement();

            virtualECC.setName(description);
            virtualECC.setAccountId(accountId);
            virtualECC.setSystemType(VirtualECCType.values()[XMLUtil.getInteger(doc, "SystemType")]);

            String zipCode = XMLUtil.getString(doc, "ZipCode");
            while (zipCode.length() < 5) zipCode = "0" + zipCode;
            virtualECC.setPostal(zipCode);

            //Figure out the timezone
            int timeZoneAdjustment = XMLUtil.getInteger(doc, "TimeZone");
            boolean useDLST = (XMLUtil.getInteger(doc, "UseDLST") == 1);
            virtualECC.setTimezone(CalendarUtils.calculateTimezone(timeZoneAdjustment, useDLST).getID());


            WeatherKey weatherKey = weatherService.findWeatherKey(virtualECC);

            if (weatherKey != null) {
                virtualECC.setWeatherId(weatherKey.getId());
            }

            virtualECC.setEnergyPlanId(energyPlanId);

            virtualECC = update(virtualECC);

            LOGGER.debug("Setting up MTUs");
            //handle the mtu array
            int numberMTU = XMLUtil.getInteger(doc, "NumberMTU");

            String mtuIdArray[] = new String[numberMTU];

            for (int i = 0; i < numberMTU; i++) {
                Element mtuElement = (Element) doc.getElementsByTagName("MTU").item(i);

                MTU mtu = new MTU();
                mtuIdArray[i] = XMLUtil.getString(mtuElement, "MTUID");
                mtu.setId(Long.parseLong(mtuIdArray[i], 16));
                mtu.setDescription(XMLUtil.getString(mtuElement, "MTUDescription"));
                if (mtu.getDescription() == null || mtu.getDescription().isEmpty()) {
                    mtu.setDescription(mtu.getHexId());
                }
                mtu.setSpyder(false);
                mtu.setMtuType(MTUType.values()[XMLUtil.getInteger(doc, "MTUType" + (i + 1))]);
                mtu.setAccountId(accountId);

                if ( (mtu.getHexId().startsWith("16") || mtu.getHexId().startsWith("17") || mtu.getHexId().startsWith("18"))){
                    mtu.setValidation(77000l);
                }
                mtudao.update(mtu);


                VirtualECCMTU virtualECCMTU = new VirtualECCMTU();
                virtualECCMTU.setMtuId(mtu.getId());
                virtualECCMTU.setVirtualECCId(virtualECC.getId());
                virtualECCMTU.setMtuType(mtu.getMtuType());
                virtualECCMTU.setMtuDescription(mtu.getDescription());
                virtualECCMTU.setAccountId(accountId);
                virtualECCMTU.setSpyder(false);
                virtualECCMTU.setVoltageMultiplier(((double) XMLUtil.getInteger(mtuElement, "VoltageCalibrationFactor")) / 1000.0);
                virtualECCMTU.setPowerMultiplier(((double) XMLUtil.getInteger(mtuElement, "PowerCalibrationFactor")) / 1000.0);
                virtualECCMTUDAO.update(virtualECCMTU);
            }


            //Get spyder data
            NodeList spyderList = doc.getElementsByTagName("Spyder");

            for (int spyderIndex = 0; spyderIndex < 4; spyderIndex++) {
                Element spyderElement = (Element) spyderList.item(spyderIndex);
                boolean enabled = XMLUtil.getInteger(spyderElement, "Enabled") == 1;
                if (!enabled) continue;

                boolean secondary = XMLUtil.getInteger(spyderElement, "Secondary") == 1;
                int mtuParent = XMLUtil.getInteger(spyderElement, "MTUParent");

                //Get group data. A group is what gets posted
                NodeList groupList = spyderElement.getElementsByTagName("Group");
                for (int groupIndex = 0; groupIndex < 8; groupIndex++) {
                    Element groupElement = (Element) groupList.item(groupIndex);
                    boolean useSpyder = XMLUtil.getInteger(groupElement, "UseCT") > 0;
                    if (!useSpyder) continue;
                    ;

                    MTU mtu = new MTU();

                    //Figure out the group id.
                    String spyderId = mtuIdArray[mtuParent];
                    int groupId = groupIndex;
                    if (secondary) groupId += 8;
                    spyderId += ("0" + Integer.toHexString(groupId));
                    mtu.setId(Long.parseLong(spyderId, 16));
                    mtu.setDescription(XMLUtil.getString(groupElement, "Description"));
                    mtu.setSpyder(true);
                    mtu.setMtuType(MTUType.STAND_ALONE);
                    mtu.setAccountId(accountId);

                    if ( mtu.getHexId().startsWith("16")){
                        mtu.setValidation(77000l);
                    }
                    mtudao.update(mtu);

                    VirtualECCMTU virtualECCMTU = new VirtualECCMTU();
                    virtualECCMTU.setMtuId(mtu.getId());
                    virtualECCMTU.setVirtualECCId(virtualECC.getId());
                    virtualECCMTU.setMtuType(mtu.getMtuType());
                    virtualECCMTU.setMtuDescription(mtu.getDescription());
                    virtualECCMTU.setAccountId(accountId);
                    virtualECCMTU.setSpyder(mtu.isSpyder());
                    virtualECCMTU.setVoltageMultiplier(1.0);
                    virtualECCMTU.setPowerMultiplier(1.0);
                    virtualECCMTUDAO.update(virtualECCMTU);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR PARSING XML FOR VIRTUAL ECC", ex);
        }




        //Go ahead and reload it.
        if (virtualECC.getId() != null) {
            LOGGER.debug("Generated virtualECC: {}", virtualECC);
            return virtualECC;
        } else return null;
    }

    public VirtualECC findDefault(String username) {
        VirtualECC virtualECC = null;
        User user = userService.findByUsername(username);
        List<AccountLocation> accountLocationList  = virtualECCDAO.findByUser(user.getId());
        for (AccountLocation accountLocation: accountLocationList){
            if (accountLocation.getAccountRole().equals(AccountRole.OWNER)){
                virtualECC = accountLocation.getVirtualECC();
                break;
            }
        }

        if (virtualECC == null && accountLocationList != null && accountLocationList.size() > 0){
            virtualECC = accountLocationList.get(0).getVirtualECC();
        }

        return virtualECC;
    }

    public VirtualECC findOneFromCache(Long locationId) {
        return virtualECCDAO.findOneFromCache(locationId);
    }

    public List<AccountLocation> findByUser(Long id) {
        return virtualECCDAO.findByUser(id);

    }

    public VirtualECC findById(Long virtualECCId) {
        return virtualECCDAO.findById(virtualECCId);
    }

    public List<VirtualECC> findByAccount(Long accountId) {
        return virtualECCDAO.findByAccount(accountId);
    }


    public void deleteById(Long virtualECCId) {
        virtualECCDAO.deleteById(virtualECCId);
        virtualECCDAO.clearCache();
    }

    @Async
    public void deleteHistory(Long virtualECCId, List<VirtualECCMTU> virtualECCMTUS){
        LOGGER.info("[deleteHistory] Deleting history data for {}", virtualECCId);
        historyMinuteDAO.deleteByVirtualECC(virtualECCId);
        historyHourDAO.deleteByVirtualECC(virtualECCId);
        historyDayDAO.deleteByVirtualECC(virtualECCId);
        historyBillingCycleDAO.deleteByVirtualECC(virtualECCId);
        for (VirtualECCMTU virtualECCMTU: virtualECCMTUS){
            LOGGER.info("[deleteHistory] Deleting data for {}", virtualECCMTU);
            historyMTUHourDAO.deleteByVirtualEcc(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId());
            historyMTUDayDAO.deleteByVirtualEcc(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId());
            historyMTUBillingCycleDAO.deleteByVirtualEcc(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId());
        }
    }
}
