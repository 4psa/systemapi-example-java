/*
 * 4PSA VoipNow - Java SOAP User: Create serviceprovider account
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */

/*
 * This file contains the test for adding a service provider account.
 */ 

 
package com._4psa.systemapi.demo.main;

import com._4psa.common_xsd._3_0_0.NoticeDocument.Notice;
import com._4psa.common_xsd._3_0_0.ResultDocument;
import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;
import com._4psa.voipnowservice._3_0_0.ServiceProviderPortStub;
import com._4psa.serviceprovidermessages_xsd._3_0_0.AddServiceProviderDocument;
import com._4psa.serviceprovidermessages_xsd._3_0_0.AddServiceProviderDocument.AddServiceProvider;
import com._4psa.serviceprovidermessages_xsd._3_0_0.AddServiceProviderResponseDocument;
import com._4psa.serviceprovidermessages_xsd._3_0_0.AddServiceProviderResponseDocument.AddServiceProviderResponse;
import com._4psa.billingdata_xsd._3_0_0.ChargingPlanList;
import java.util.Random;


/**
 * Test for pinging the VoipServer and creating a Service Provider account
 */
public class TestAddServiceProviderAccount {
	/**
	 * Creates a test which creates a service provider account.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the ServiceProvider stub.
	 */
	public TestAddServiceProviderAccount(UserCredentialsDocument userCredentials) throws Exception {
		/* initialize the ServiceProvider stub */
		ServiceProviderPortStub serviceProviderStub = new ServiceProviderPortStub(Constants.VOIPNOW_URL + "soap2/sp_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(serviceProviderStub);
		
		/* create a serviceprovider account */
		this.addAccount(serviceProviderStub, userCredentials);
	}
	
	/**
	 * Adds a serviceProvider account.
	 * @param ServiceProviderPortStub stub - the ServiceProvider stub.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the Billing stub by calling "CommonOperations.getChargingPlans()" method.
	 */
	public final void addAccount(ServiceProviderPortStub stub, UserCredentialsDocument userCredentials) throws Exception {
		AddServiceProvider add_sp = AddServiceProvider.Factory.newInstance();
		
		/* the charging plans which may be assigned to the service provider(the charging plans created by the admin) */
		ChargingPlanList[] chargingPlans = CommonOperations.getChargingPlans(userCredentials);
		/* if there are some charging plans created by the admin, choose one randomly and set it to the service provider */
		if (chargingPlans.length > 0) {
			Random randomizer = new Random();
			int chosenOne = randomizer.nextInt(chargingPlans.length);
			ChargingPlanList chargingPlan = chargingPlans[chosenOne];
			/* setup charging plan */
			add_sp.setChargingPlanID(chargingPlan.getID());
		}

		/* company */
		add_sp.setCompany(Constants.COMPANY);
		/* serviceprovider name */
		add_sp.setName(Constants.NAME);
		/* login username */
		add_sp.setLogin(Constants.SERVICE_PROVIDER_LOGIN);
		/* generate auto password */
		add_sp.setPasswordAuto(Constants.PASSWORD_AUTO_GENERATION);
		/* setup a specific password if the auto generation is disabled */
		if (Constants.PASSWORD_AUTO_GENERATION == false) {
			add_sp.setPassword(Constants.PASSWORD);
		}
		
		/* phone number: public number or extension number */
		add_sp.setPhone(Constants.PHONE_NUMBER);
		/* fax */
		add_sp.setFax(Constants.FAX);
		/* email */
		add_sp.setEmail(Constants.EMAIL);
		/* address */
		add_sp.setAddress(Constants.ADDRESS);
		/* city */
		add_sp.setCity(Constants.CITY);
		/* postal code */
		add_sp.setPcode(Constants.POSTAL_CODE);
		/* country code */
		add_sp.setCountry(Constants.COUNTRY_CODE);
		/* interface language code */
		add_sp.setInterfaceLang(Constants.INTERFACE_LANG_CODE);
		/* notes */
		add_sp.setNotes(Constants.NOTES);
		/* notifyOnly */
		add_sp.setNotifyOnly(Constants.NOTIFY_ONLY); 
		/* scope */
		add_sp.setScope(Constants.SCOPE_IDENTIFIER);
		/* deployment keeping unit */
		add_sp.setLinkUUID(Constants.DEPLOYMENT_KEEPING_UNIT);
		
		AddServiceProviderDocument aspd = AddServiceProviderDocument.Factory.newInstance();
		aspd.setAddServiceProvider(add_sp);
		/* make the call */
		try {
			/* the response */
			AddServiceProviderResponseDocument response_document = stub.addServiceProvider(aspd, userCredentials);
			AddServiceProviderResponse response = response_document.getAddServiceProviderResponse();
			/* information from the response */
			System.out.println("Add ServiceProvider Account response:");
			System.out.println("\toperation status: " + response.getResult());
			if (response.getResult() ==  ResultDocument.Result.Enum.forString("success")) {
				System.out.println("\tThe following service provider was created: id:" + response.getID() +
					" login:" + response.getLogin());
			}
			/* notices received */
			System.out.println("\tnotices:");
			Notice[] notices = response.getNoticeArray();
			for (int i = 0; i < notices.length; i++) {
				System.out.println("\t\t code: " + notices[i].getCode() +" - "+ notices[i].getMessage());
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
}