/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.AccountMembers;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;

/**
 * Created by pete on 6/30/2014.
 */
public interface AccountServiceClient extends RestService {
    @PUT
    @Path("account/{accountId}")
    public void updateAccount(Account account, @PathParam("accountId") long accountId, MethodCallback<Void> callBack);

    @GET
    @Path("account/{accountId}/members")
    public void getAccountMembers(@PathParam("accountId") long accountId, MethodCallback<AccountMembers> callBack);

    @PUT
    @Path("account/{accountId}/member/{accountMemberId}")
    public void updateAccountMember(AccountMember accountMember, @PathParam("accountId") long accountId, @PathParam("accountMemberId") long accountMemberId, MethodCallback<Void> callBack);

    @POST
    @Path("account/{accountId}/member")
    public void addAccountMember(AccountMember accountMember, @PathParam("accountId") long accountId, MethodCallback<Void> callBack);

    @DELETE
    @Path("account/{accountId}/member/{accountMemberId}")
    public void deleteAccountMember(@PathParam("accountId") long accountId, @PathParam("accountMemberId") long accountMemberId, MethodCallback<Void> callBack);


}
