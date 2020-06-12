/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.*;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.ConfirmEvent;
import com.ted.commander.client.events.ConfirmHandler;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.AdviceEditPlace;
import com.ted.commander.client.places.AdviceListPlace;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.widgets.dialogs.ConfirmDialog;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.*;
import com.vaadin.polymer.change.widget.event.ChangeEvent;
import com.vaadin.polymer.change.widget.event.ChangeEventHandler;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AdviceEditPresenter implements AdviceEditView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AdviceEditPresenter.class.getName());

    final ClientFactory clientFactory;
    final AdviceEditView view;
    final Place place;
    final long adviceId;
    Advice advice;
    EnergyPlan locationEnergyPlan = null;
    private AccountMembership accountMembership;

    List<AccountMember> accountMemberList = null;


    public AdviceEditPresenter(final ClientFactory clientFactory, AdviceEditPlace place) {
        LOGGER.fine("CREATING NEW AdviceEditPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getAdviceEditView();
        view.setPresenter(this);
        this.adviceId = place.getAdviceId();


        RESTFactory.getAdviceService(clientFactory).get(adviceId, new DefaultMethodCallback<Advice>() {
            @Override
            public void onSuccess(Method method, final Advice advice) {
                RESTFactory.getEnergyPlanService(clientFactory).getForLocation(advice.getVirtualECCId(), new DefaultMethodCallback<EnergyPlan>() {
                    @Override
                    public void onSuccess(Method method, EnergyPlan energyPlan) {
                        locationEnergyPlan = energyPlan;


                        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
                            @Override
                            public void onSuccess(Method method, AccountMemberships accountMemberships) {
                                boolean found = false;
                                for (AccountMembership accountMembership : accountMemberships.getAccountMemberships()) {
                                    if (accountMembership.getAccount().getId().equals(advice.getAccountId())) {
                                        found = true;
                                        setAccountMembership(accountMembership);
                                        setAdvice(advice);
                                        break;
                                    }
                                }
                                if (!found) {
                                    goTo(new AdviceListPlace(""));
                                }
                            }
                        });

                    }
                });

            }
        });


        view.locationPicker().addValueChangeHandler(new ValueChangeHandler<AccountLocation>() {
            @Override
            public void onValueChange(ValueChangeEvent<AccountLocation> valueChangeEvent) {
                save();
            }
        });

        view.adviceName().addChangeHandler(new ChangeEventHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                save();
            }
        });


    }


    private void save() {
        if (isValid()) {
            advice.setAdviceName(view.adviceName().getValue());
            advice.setVirtualECCId(view.locationPicker().getValue().getVirtualECC().getId());
            RESTFactory.getAdviceService(clientFactory).put(advice, new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    view.showToast();
                }
            });
        }
    }

    public void setAdvice(final Advice advice) {
        this.advice = advice;
        view.adviceName().setValue(advice.getAdviceName());
        view.setRecipients(advice.getAdviceRecipientList());
        view.setTriggers(advice.getTriggerList());

        RESTFactory.getVirtualECCService(clientFactory).getForAccount(advice.getAccountId(), new DefaultMethodCallback<List<VirtualECC>>() {
            @Override
            public void onSuccess(Method method, List<VirtualECC> virtualECCs) {
                List<AccountLocation> accountLocations = new ArrayList<AccountLocation>();
                AccountLocation selectedLocation = null;
                for (VirtualECC virtualECC : virtualECCs) {
                    AccountLocation accountLocation = new AccountLocation();
                    accountLocation.setAccountName("");
                    accountLocation.setAccountRole(AccountRole.READ_ONLY);
                    accountLocation.setVirtualECC(virtualECC);
                    if (virtualECC.getId().equals(advice.getVirtualECCId())) {
                        selectedLocation = accountLocation;
                    }
                    accountLocations.add(accountLocation);
                }
                view.setLocationList(accountLocations);
                if (selectedLocation == null) selectedLocation = accountLocations.get(0);
                view.locationPicker().setValue(selectedLocation, false);
            }
        });

    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        view.adviceName().setInvalid(false);

        if (view.adviceName().getValue().trim().length() == 0) {
            valid = false;
            view.adviceName().setInvalid(true);
            view.adviceName().setErrorMessage(WebStringResource.INSTANCE.requiredField());
        }

        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void goTo(Place destinationPage) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, new ComparisonPlace(""), null));
    }

    @Override
    public void onResize() {

    }

    @Override
    public void deleteAdvice() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.open("Delete?", "Are you sure you want to delete this advice");
        confirmDialog.addConfirmHandler(new ConfirmHandler() {
            @Override
            public void onConfirm(ConfirmEvent event) {
                if (event.getConfirmed()) {
                    LOGGER.fine("Deleting Advice " + advice.getId());
                    RESTFactory.getAdviceService(clientFactory).delete(advice, new DefaultMethodCallback<Void>() {
                        @Override
                        public void onSuccess(Method method, Void aVoid) {
                            goTo(new AdviceListPlace(advice.getAccountId()));

                        }
                    });
                }
            }
        });
    }

    @Override
    public void goBack() {
        if (isValid()) {
            goTo(new AdviceListPlace(advice.getAccountId()));
        }
    }

    @Override
    public void editRecipient(final AdviceRecipient item) {

        boolean canEdit = accountMembership.getAccountRole().equals(AccountRole.READ_ONLY) && item.getUserId().equals(clientFactory.getUser().getId());
        canEdit = canEdit | (!accountMembership.getAccountRole().equals(AccountRole.READ_ONLY));

        EditRecipientDialog editRecipientDialog = new EditRecipientDialog(item, !canEdit);
        editRecipientDialog.addItemChangedHandler(new ItemChangedHandler<AdviceRecipient>() {
            @Override
            public void onChanged(ItemChangedEvent<AdviceRecipient> event) {
                RESTFactory.getAdviceService(clientFactory).put(event.getItem(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        refreshAdvice();
                    }
                });
            }
        });

        editRecipientDialog.addItemDeletedHandler(new ItemDeletedHandler<AdviceRecipient>() {
            @Override
            public void onDeleted(ItemDeletedEvent<AdviceRecipient> event) {
                RESTFactory.getAdviceService(clientFactory).delete(event.getItem(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        refreshAdvice();
                    }
                });
            }
        });

        editRecipientDialog.open();

    }


    private void refreshAdvice() {
        RESTFactory.getAdviceService(clientFactory).get(advice.getId(), new DefaultMethodCallback<Advice>() {
            @Override
            public void onSuccess(Method method, Advice advice) {
                setAdvice(advice);
            }
        });
    }

    private void showCreateDialog() {
        CreateRecipientDialog createRecipientDialog = new CreateRecipientDialog(advice, accountMemberList);
        createRecipientDialog.addItemCreatedHandler(new ItemCreatedHandler<AdviceRecipient>() {
            @Override
            public void onCreated(ItemCreatedEvent<AdviceRecipient> event) {
                RESTFactory.getAdviceService(clientFactory).put(event.getItem(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        refreshAdvice();
                    }
                });
            }
        });
        createRecipientDialog.open();
    }

    @Override
    public void createRecipient() {
        if (this.accountMemberList == null) {
            RESTFactory.getAccountService(clientFactory).getAccountMembers(advice.getAccountId(), new DefaultMethodCallback<AccountMembers>() {
                @Override
                public void onSuccess(Method method, AccountMembers accountMembers) {
                    accountMemberList = accountMembers.getAccountMembers();
                    showCreateDialog();
                }
            });
        } else {
            showCreateDialog();
        }

    }

    @Override
    public void createTrigger() {
        editTrigger(new AdviceTrigger(advice.getId()));
    }


    private void showTriggerDialog(EnergyPlan energyPlan, AdviceTrigger adviceTrigger){
        boolean canEdit = !accountMembership.getAccountRole().equals(AccountRole.READ_ONLY);
        final TriggerDialog triggerDialog = new TriggerDialog(advice, adviceTrigger, canEdit, energyPlan);

        triggerDialog.addCreateHandler(new ItemCreatedHandler<AdviceTrigger>() {
            @Override
            public void onCreated(ItemCreatedEvent<AdviceTrigger> event) {
                RESTFactory.getAdviceService(clientFactory).put(event.getItem(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        triggerDialog.close();
                        refreshAdvice();

                    }
                });
            }
        });

        triggerDialog.addChangeHandler(new ItemChangedHandler<AdviceTrigger>() {
            @Override
            public void onChanged(ItemChangedEvent<AdviceTrigger> event) {
                RESTFactory.getAdviceService(clientFactory).put(event.getItem(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        triggerDialog.close();
                        refreshAdvice();
                    }
                });
            }
        });

        triggerDialog.addDeletedHandler(new ItemDeletedHandler<AdviceTrigger>() {
            @Override
            public void onDeleted(ItemDeletedEvent<AdviceTrigger> event) {
                RESTFactory.getAdviceService(clientFactory).delete(event.getItem(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        triggerDialog.close();
                        refreshAdvice();
                    }
                });
            }
        });

        triggerDialog.open();
    }

    @Override
    public void editTrigger(final AdviceTrigger adviceTrigger) {
        showTriggerDialog(locationEnergyPlan, adviceTrigger);
    }

    @Override
    public EnergyPlan getEnergyPlan() {
        return locationEnergyPlan;
    }

    public void setAccountMembership(AccountMembership accountMembership) {
        this.accountMembership = accountMembership;
        view.setReadOnly (this.accountMembership.getAccountRole().equals(AccountRole.READ_ONLY));

    }

    public AccountMembership getAccountMembership() {
        return accountMembership;
    }
}
