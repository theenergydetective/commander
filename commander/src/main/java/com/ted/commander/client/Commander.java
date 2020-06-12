/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.widget.animation.AnimatableDisplay;
import com.googlecode.mgwt.ui.client.widget.animation.AnimationWidget;
import com.petecode.common.client.widget.paper.PaperStyleBundle;
import com.petecode.common.client.widget.paper.PaperStyleManager;
import com.ted.commander.client.activities.CommanderActivityMapper;
import com.ted.commander.client.activities.CommanderAnimationMapper;
import com.ted.commander.client.activities.CommanderHistoryMapper;
import com.ted.commander.client.analytics.GoogleAnalytics;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.clientFactory.ClientFactoryImpl;
import com.ted.commander.client.clientFactory.ClientFactoryMobileImpl;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.events.PlaceRequestHandler;
import com.ted.commander.client.places.ConfirmEmailPlace;
import com.ted.commander.client.places.JoinPlace;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.places.ResetPasswordPlace;
import com.ted.commander.client.restInterface.RESTFactory;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.elemental.Function;
import com.vaadin.polymer.iron.element.IronIconElement;
import com.vaadin.polymer.paper.element.*;
import org.fusesource.restygwt.client.Method;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Commander implements EntryPoint {


    private static Logger LOGGER = Logger.getLogger(Commander.class.getName());
    public static GoogleAnalytics googleAnalytics = new GoogleAnalytics();

    final AnimatableDisplay appWidget = new AnimationWidget();
    //final ThemedContainer appWidget = new ThemedContainer();



    final CommanderHistoryMapper historyMapper = GWT.create(CommanderHistoryMapper.class);
    static final PaperStyleBundle paperStyleBundle = GWT.create(PaperStyleBundle.class);
    public static ClientFactory clientFactory;
    PlaceHistoryHandler historyHandler;


    PlaceRequestHandler placeRequestHandler = new PlaceRequestHandler() {
        @Override
        public void onRequest(PlaceRequestEvent event) {
            //gotoAuthorizedPlace(event.getDestinationPage());
            clientFactory.getController().goTo(event.getDestinationPage());
            String token = historyMapper.getToken(event.getDestinationPage());
            History.newItem(token);
        }
    };


    ResizeHandler windowResizeHandler = new ResizeHandler() {
        @Override
        public void onResize(ResizeEvent resizeEvent) {
            LOGGER.fine("ONRESIZE");
            if (MGWT.getOsDetection().isDesktop()) {
                clientFactory.onResize(resizeEvent);
            }
        }
    };


    public void onModuleLoad() {
        LOGGER.info("ON MODULE LOAD");

        //Polymer.startLoading();

        // Although gwt-polymer-elements takes care of dynamic loading of components
        // if they are created using Polymer.createElement or Polymer Widgets,
        // there are certain features which must be loaded previously to start
        // the application. Hence you have to add import tags to your host page or
        // import those additional features dynamically, in this case it might be
        // necessary to wait until the components are ready.
        // The `Polymer` utility class provide a set of methods for facilitating it,
        // you can pass tag-names for standard component locations (tag-name/tag-name.html)
        // or relative urls otherwise. Also you can pass success and error call-backs.

        // Paper applications must always import paper-styles

        LOGGER.fine("IMPORT HREF");
        Polymer.importHref(Arrays.asList(
                "paper-styles",
                RESTFactory.getThemeUrl(),
                "paper-styles/typography.html",
                "iron-icons/iron-icons.html",
                "iron-icons/communication-icons.html",
                "iron-icons/social-icons.html",
                "iron-icons/hardware-icons.html",
                "iron-icons/av-icons.html",
                PaperCheckboxElement.SRC,
                PaperDrawerPanelElement.SRC,
                PaperHeaderPanelElement.SRC,
                PaperRadioButtonElement.SRC,
                PaperRadioGroupElement.SRC,
                PaperToastElement.SRC,
                PaperSliderElement.SRC,
                PaperTabsElement.SRC,
                PaperTabElement.SRC,
                PaperMaterialElement.SRC,
                PaperSpinnerElement.SRC,
                PaperTabElement.SRC,
                PaperToolbarElement.SRC,
                PaperIconItemElement.SRC,
                PaperIconButtonElement.SRC,
                PaperRippleElement.SRC,
                PaperDialogElement.SRC,
                PaperToggleButtonElement.SRC,
                IronIconElement.SRC), new Function() {
            public Object call(Object arg) {

                // The app is executed when all imports succeed.
                LOGGER.info("ICONS LOADED. STARTING APPLICATION");

                GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
                    @Override
                    public void onUncaughtException(Throwable throwable) {
                        LOGGER.log(Level.SEVERE, throwable.toString(), throwable);
                    }
                });

                MGWTSettings settings = new MGWTSettings();
                settings.setFullscreen(true);
                // set viewport and other settings for mobile
                MGWT.applySettings(MGWTSettings.getAppSetting());


                if (MGWT.getOsDetection().isDesktop()) {
                    clientFactory = new ClientFactoryImpl();
                    Window.addResizeHandler(windowResizeHandler);
                    settings.setPreventScrolling(false);
                } else {
                    settings.setPreventScrolling(true);
                    clientFactory = new ClientFactoryMobileImpl();
                }


                clientFactory.getEventBus().addHandler(PlaceRequestEvent.TYPE, placeRequestHandler);

                // Start ActivityManager for the main widget with our ActivityMapper
                AnimatingActivityManager activityManager = new AnimatingActivityManager(new CommanderActivityMapper(clientFactory), new CommanderAnimationMapper(), clientFactory.getEventBus());
                //ActivityManager activityManager = new ActivityManager(new CommanderActivityMapper(clientFactory), clientFactory.getEventBus());
                activityManager.setDisplay(appWidget);

                // Start PlaceHistoryHandler with our PlaceHistoryMapper
                historyHandler = new PlaceHistoryHandler(historyMapper);


                RootPanel.get().add(appWidget);
                // Goes to the place represented on URL else default place

//                //Set up phonegap
//                PhoneGap phoneGap = GWT.create(PhoneGap.class);
//                phoneGap.addHandler(new PhoneGapAvailableHandler() {
//                    @Override
//                    public void onPhoneGapAvailable(PhoneGapAvailableEvent event) {
//                        //start your app - phonegap is ready
//
//
//                    }
//                });
//
//                phoneGap.addHandler(new PhoneGapTimeoutHandler() {
//                    @Override
//                    public void onPhoneGapTimeout(PhoneGapTimeoutEvent event) {
//                        //can not start phonegap - something is for with your setup
//                        OKDialog okDialog = new OKDialog(WebStringResource.INSTANCE.error(), "Could not initialize PhoneGap");
//                        okDialog.center();
//                    }
//                });
//                phoneGap.initializePhoneGap();


                LOGGER.fine("STARTING APP");

                paperStyleBundle.css().ensureInjected();
                PaperStyleManager.setTheme();
                PaperStyleBundle paperStyleBundle = GWT.create(PaperStyleBundle.class);
                paperStyleBundle.css().ensureInjected();


                historyHandler.register(clientFactory.getController(), clientFactory.getEventBus(), new LoginPlace(""));

                if (!isActivation() && !isInvitation() && !isReset()) {
                    //LOGGER.fine("DEFAULT LOGIN");
                    //clientFactory.getController().goTo(new LoginPlace(""));
                    historyHandler.handleCurrentHistory();
                }

                LOGGER.fine("Starting application");


                return null;
            }
        });


    }

    private boolean isInvitation() {
        if (Window.Location.getParameterMap().size() != 0) {
            final String inviteKey = Window.Location.getParameter("inviteKey");
            if (inviteKey != null && !inviteKey.isEmpty()) {
                LOGGER.fine("INVITE KEY DETECTED: " + inviteKey);
                clientFactory.getController().goTo(new JoinPlace(""));
                return true;
            }
        }
        return false;
    }


    private boolean isActivation() {
        if (Window.Location.getParameterMap().size() != 0) {
            final String accountActivationKey = Window.Location.getParameter("accountActivationKey");
            final String userName = Window.Location.getParameter("username");
            LOGGER.fine("accountActivationKey:" + accountActivationKey);
            if (userName != null && !userName.isEmpty() && accountActivationKey != null && !accountActivationKey.isEmpty()) {
                LOGGER.fine("ACTIVATION KEY DETECTED: " + accountActivationKey);
                RESTFactory.getUserService(clientFactory).activateUser(accountActivationKey, new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        LOGGER.fine("ACTIVATION COMPLETE:" + method.getResponse().getStatusCode());
                        clientFactory.getController().goTo(new ConfirmEmailPlace(""));
                    }
                });
                return true;
            }
        }
        return false;
    }

    private boolean isReset() {
        if (Window.Location.getParameterMap().size() != 0) {
            final String resetKey= Window.Location.getParameter("resetPassword");
            if (resetKey != null && !resetKey.trim().isEmpty()){
                clientFactory.getController().goTo(new ResetPasswordPlace(resetKey));
                return true;
            }
            return false;
        }
        return false;
    }


}
