/*
 * 4PSA VoipNow - Java SOAP User: Create user account
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */
 
/*
 * This file contains the test for adding an extension account.
 */ 

 
package com._4psa.systemapi.demo.main;

import com._4psa.common_xsd._3_0_0.NoticeDocument.Notice;
import com._4psa.common_xsd._3_0_0.ResultDocument;
import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;
import com._4psa.voipnowservice._3_0_0.ExtensionPortStub;
import com._4psa.voipnowservice._3_0_0.UserPortStub;
import com._4psa.extensionmessages_xsd._3_0_0.*;
import com._4psa.extensionmessages_xsd._3_0_0.AddExtensionResponseDocument.AddExtensionResponse;
import com._4psa.extensiondata_xsd._3_0_0.ExtensionTypeDocument.ExtensionType;
import com._4psa.extensionmessages_xsd._3_0_0.AddExtensionDocument.AddExtension;
import com._4psa.usermessages_xsd._3_0_0.GetUsersDocument;
import com._4psa.usermessages_xsd._3_0_0.GetUsersDocument.GetUsers;
import com._4psa.usermessages_xsd._3_0_0.GetUsersResponseDocument;
import com._4psa.usermessages_xsd._3_0_0.GetUsersResponseDocument.GetUsersResponse;
import com._4psa.userdata_xsd._3_0_0.UserList;
import java.rmi.RemoteException;
import java.util.Random;


/**
 * Test for creating an extension account
 */
public class TestAddExtensionAccount {
	/**
	 * Creates a test which creates an extension account.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the Extension stub.
	 */
	public TestAddExtensionAccount(UserCredentialsDocument userCredentials) throws Exception {
		/* initialize the Extension stub */
		ExtensionPortStub extensionStub = new ExtensionPortStub(Constants.VOIPNOW_URL + "soap2/extension_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(extensionStub);
		
		/* create an extension account */
		this.addAccount(extensionStub, userCredentials);
	}
	
	/**
	 * Adds an extension account
	 * @param stub - the Extension stub
	 * @param userCredentials - the user credentials document
	 * @throws org.apache.axis2.AxisFault - when initializing the User stub by calling "this.getUsers" method.
	 */
	public final void addAccount(ExtensionPortStub stub, UserCredentialsDocument userCredentials) throws Exception {
		AddExtension add_extension = AddExtension.Factory.newInstance();
		
		Integer parentId = null;
		/* choose an user to be the parent of this extension */
		UserList[] users = this.getUsers(userCredentials);
		if (users.length > 0) {
			Random randomizer = new Random();
			int chosenUser = randomizer.nextInt(users.length);
			UserList user = users[chosenUser];
			parentId = new Integer(user.getID());
			/* setup the parent */
			add_extension.setParentID(parentId.intValue());            
		}
		
		/* the type of the extension */
		ExtensionType.Enum enu = ExtensionType.Enum.forString(Constants.EXTENSION_TYPE);
		add_extension.setExtensionType(enu);
		
		/* the label */
		add_extension.setLabel(Constants.EXTENSION_LABEL);
		
		/* generate auto password */
		add_extension.setPasswordAuto(Constants.PASSWORD_AUTO_GENERATION);
		/* setup a specific password if the auto generation is disabled */
		if (Constants.PASSWORD_AUTO_GENERATION == false) {
			add_extension.setPassword(Constants.PASSWORD);
		}
		
		AddExtensionDocument extensionDoc = AddExtensionDocument.Factory.newInstance();
		extensionDoc.setAddExtension(add_extension);
		/* make the call */
		try {
			AddExtensionResponseDocument responseDocument = stub.addExtension(extensionDoc, userCredentials);
			/* information from the response */
			AddExtensionResponse response = responseDocument.getAddExtensionResponse();
			
			/* notices received */
			Notice[] notices = response.getNoticeArray();
			
			System.out.println("Add Extension Account response:");
			System.out.println("\toperation status: " + response.getResult());
			if (response.getResult() ==  ResultDocument.Result.Enum.forString("success")) {
				System.out.println("\tThe following extension was created: extended number:" + response.getExtendedNumber());
			}
			System.out.println("\tnotices:");
			for (int i = 0; i < notices.length; i++) {
				System.out.println("\t\t code: " + notices[i].getCode() +" - "+ notices[i].getMessage());
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Returns the users.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @return UserList[] array with the users.
	 * @throws org.apache.axis2.AxisFault - when initializing the User stub.
	 */
	public final UserList[] getUsers(UserCredentialsDocument userCredentials) throws Exception {
		/* the User stub */
		UserPortStub stub = new UserPortStub(Constants.VOIPNOW_URL + "soap2/user_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(stub);
		
		GetUsersDocument gud = GetUsersDocument.Factory.newInstance();
		GetUsers usersRequest = GetUsers.Factory.newInstance();
		gud.setGetUsers(usersRequest);
		
		UserList[] results = null;
		/* make the call */
		try {
			/* the response */
			GetUsersResponseDocument responseDocument = stub.getUsers(gud, userCredentials);
			GetUsersResponse response = responseDocument.getGetUsersResponse();
			results = response.getUserArray();
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
		
		return results;
	}
}