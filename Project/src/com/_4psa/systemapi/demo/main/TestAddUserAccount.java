/*
 * 4PSA VoipNow - Java SOAP User: Create user account
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */

/*
 * This file contains the test for adding an user account.
 */ 

 
package com._4psa.systemapi.demo.main;

import com._4psa.common_xsd._3_0_0.NoticeDocument.Notice;
import com._4psa.common_xsd._3_0_0.ResultDocument;
import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;
import com._4psa.voipnowservice._3_0_0.UserPortStub;
import com._4psa.voipnowservice._3_0_0.OrganizationPortStub;
import com._4psa.usermessages_xsd._3_0_0.*;
import com._4psa.usermessages_xsd._3_0_0.AddUserDocument.AddUser;
import com._4psa.usermessages_xsd._3_0_0.AddUserResponseDocument.AddUserResponse;
import com._4psa.organizationmessages_xsd._3_0_0.GetOrganizationsDocument;
import com._4psa.organizationmessages_xsd._3_0_0.GetOrganizationsDocument.GetOrganizations;
import com._4psa.organizationmessages_xsd._3_0_0.GetOrganizationsResponseDocument;
import com._4psa.organizationmessages_xsd._3_0_0.GetOrganizationsResponseDocument.GetOrganizationsResponse;
import com._4psa.organizationdata_xsd._3_0_0.OrganizationList;
import com._4psa.billingdata_xsd._3_0_0.ChargingPlanList;
import java.rmi.RemoteException;
import java.util.Random;


/**
 * Test for creating an user account
 */
public class TestAddUserAccount {
	/**
	 * Creates a test which creates an user account.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the User stub.
	 */
	public TestAddUserAccount(UserCredentialsDocument userCredentials) throws Exception {
		/* initialize the User stub */
		UserPortStub userStub = new UserPortStub(Constants.VOIPNOW_URL + "soap2/user_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(userStub);
		
		/* create an user account */
		this.addAccount(userStub, userCredentials);
	}
	
	/**
	 * Adds a user account
	 * @param UserPortStub stub - the User stub
	 * @param UserCredentialsDocument userCredentials - the user credentials document.
	 * @throws org.apache.axis2.AxisFault - when initializing the Organization stub by calling "this.getOrganizations()" method.
	 */
	public final void addAccount(UserPortStub stub, UserCredentialsDocument userCredentials) throws Exception {
		AddUser add_user = AddUser.Factory.newInstance();
		
		Integer parentId = null;
		/* choose an organization to be the parent of this user */
		OrganizationList[] organizations = this.getOrganizations(userCredentials);
		if (organizations.length > 0) {
		    Random randomizer = new Random();
			int chosenOrg = randomizer.nextInt(organizations.length);
			OrganizationList organization = organizations[chosenOrg];
			parentId = new Integer(organization.getID());
			/* setup the parent */
			add_user.setParentID(parentId.intValue());
				
			/* the charging plans which may be assigned to the user(the charging plans created by the parent) */
			ChargingPlanList[] chargingPlans = CommonOperations.getChargingPlans(parentId.intValue(), userCredentials);
			/* if there are some charging plans created by the parent, choose one randomly and set it to the user */
			if (chargingPlans.length > 0) {
				int chosenCP = randomizer.nextInt(chargingPlans.length);
				ChargingPlanList chargingPlan = chargingPlans[chosenCP];
				/* setup charging plan */
				add_user.setChargingPlanID(chargingPlan.getID());
			}
		}

		/* company */
		add_user.setCompany(Constants.COMPANY);
		/* name */
		add_user.setName(Constants.NAME);
		/* username */
		add_user.setLogin(Constants.USER_LOGIN);
		/* generate auto password */
		add_user.setPasswordAuto(Constants.PASSWORD_AUTO_GENERATION);
		/* setup a specific password if the auto generation is disabled */
		if (Constants.PASSWORD_AUTO_GENERATION == false) {
			add_user.setPassword(Constants.PASSWORD);
		}
		
		/* phone number: public number or extension number */
		add_user.setPhone(Constants.PHONE_NUMBER);
		/* fax */
		add_user.setFax(Constants.FAX);
		/* email */
		add_user.setEmail(Constants.EMAIL);
		/* address */
		add_user.setAddress(Constants.ADDRESS);
		/* city */
		add_user.setCity(Constants.CITY);
		/* postal code */
		add_user.setPcode(Constants.POSTAL_CODE);
		/* country code */
		add_user.setCountry(Constants.COUNTRY_CODE);
		/* interface language code */
		add_user.setInterfaceLang(Constants.INTERFACE_LANG_CODE);
		/* scope */
		add_user.setScope(Constants.SCOPE_IDENTIFIER);
		
		AddUserDocument userDoc = AddUserDocument.Factory.newInstance();
		userDoc.setAddUser(add_user);
		/* make the call */
		try {
			AddUserResponseDocument responseDocument = stub.addUser(userDoc, userCredentials);
			/* information from the response */
			AddUserResponse response = responseDocument.getAddUserResponse();
			
			/* notices received */
			Notice[] notices = response.getNoticeArray();
			
			System.out.println("Add User Account response:");
			System.out.println("\toperation status: " + response.getResult());
			if (response.getResult() ==  ResultDocument.Result.Enum.forString("success")) {
				System.out.println("\tThe following user was created: id:" + response.getID() +
					" login:" + response.getLogin());
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
	 * Returns the organizations.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @return OrganizationList[] array with the organizations.
	 * @throws org.apache.axis2.AxisFault - when initializing the Organization stub.
	 */
	public final OrganizationList[] getOrganizations(UserCredentialsDocument userCredentials) throws Exception {
		/* the Organization stub */
		OrganizationPortStub stub = new OrganizationPortStub(Constants.VOIPNOW_URL + "soap2/organization_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(stub);
		
		GetOrganizationsDocument god = GetOrganizationsDocument.Factory.newInstance();
		GetOrganizations organizationsRequest = GetOrganizations.Factory.newInstance();
		god.setGetOrganizations(organizationsRequest);
		
		OrganizationList[] results = null;
		/* make the call */
		try {
			/* the response */
			GetOrganizationsResponseDocument responseDocument = stub.getOrganizations(god, userCredentials);
			GetOrganizationsResponse response = responseDocument.getGetOrganizationsResponse();
			results = response.getOrganizationArray();
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
		
		return results;
	}
}