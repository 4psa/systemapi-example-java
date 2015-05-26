/*
 * 4PSA VoipNow - Java SOAP User: Create an organization account
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */
 
/*
 * This file contains the test for adding an organization account.
 */ 


package com._4psa.systemapi.demo.main;

import com._4psa.common_xsd._3_0_0.NoticeDocument.Notice;
import com._4psa.common_xsd._3_0_0.ResultDocument;
import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;
import com._4psa.voipnowservice._3_0_0.OrganizationPortStub;
import com._4psa.voipnowservice._3_0_0.ServiceProviderPortStub;
import com._4psa.organizationmessages_xsd._3_0_0.AddOrganizationDocument;
import com._4psa.organizationmessages_xsd._3_0_0.AddOrganizationDocument.AddOrganization;
import com._4psa.organizationmessages_xsd._3_0_0.AddOrganizationResponseDocument;
import com._4psa.organizationmessages_xsd._3_0_0.AddOrganizationResponseDocument.AddOrganizationResponse;
import com._4psa.serviceprovidermessages_xsd._3_0_0.GetServiceProvidersDocument;
import com._4psa.serviceprovidermessages_xsd._3_0_0.GetServiceProvidersDocument.GetServiceProviders;
import com._4psa.serviceprovidermessages_xsd._3_0_0.GetServiceProvidersResponseDocument;
import com._4psa.serviceproviderdata_xsd._3_0_0.ServiceProviderList;
import com._4psa.serviceprovidermessages_xsd._3_0_0.GetServiceProvidersResponseDocument.GetServiceProvidersResponse;
import com._4psa.billingdata_xsd._3_0_0.ChargingPlanList;
import java.rmi.RemoteException;
import java.util.Random;


/**
 * Test for pinging the VoipServer and creating a Service Provider account
 */
public class TestAddOrganizationAccount {
	/**
	 * Creates a test which creates an organization account.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the Organization stub.
	 */
	public TestAddOrganizationAccount(UserCredentialsDocument userCredentials) throws Exception {
		/* initialize the Organization stub */
		OrganizationPortStub organizationStub = new OrganizationPortStub(Constants.VOIPNOW_URL + "soap2/organization_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(organizationStub);
		
		/* create an organization account */
		this.addAccount(organizationStub, userCredentials);
	}
	
	/**
	 * Adds an organization account
	 * @param OrganizationPortStub stub - the Organization stub
	 * @param UserCredentialsDocument userCredentials - the user credentials
	 * @throws org.apache.axis2.AxisFault - when initializing the ServiceProvider stub by calling "this.getServiceProvider()" method.
	 */
	public final void addAccount(OrganizationPortStub stub, UserCredentialsDocument userCredentials) throws Exception {
		AddOrganization add_org = AddOrganization.Factory.newInstance();
		
		Integer parentId = null;
		/* choose a service provider to be the parent of this organization */
		ServiceProviderList[] serviceProviders = this.getServiceProviders(userCredentials);
		if (serviceProviders.length > 0) {
			Random randomizer = new Random();
			int chosenSP = randomizer.nextInt(serviceProviders.length);
			ServiceProviderList serviceProvider = serviceProviders[chosenSP];
			parentId = new Integer(serviceProvider.getID());
			/* setup the parent */
			add_org.setParentID(parentId.intValue());
			
			/* the charging plans which may be assigned to the organization(the charging plans created by the parent) */
			ChargingPlanList[] chargingPlans = CommonOperations.getChargingPlans(parentId.intValue(), userCredentials);
			/* if there are some charging plans created by the parent, choose one randomly and set it to the organization */
			if (chargingPlans.length > 0) {
				int chosenCP = randomizer.nextInt(chargingPlans.length);
				ChargingPlanList chargingPlan = chargingPlans[chosenCP];
				/* setup charging plan */
				add_org.setChargingPlanID(chargingPlan.getID());
			}
		}
		
		/* company */
		add_org.setCompany(Constants.COMPANY);
		/* organization name */
		add_org.setName(Constants.NAME);
		/* login username */
		add_org.setLogin(Constants.ORGANIZATION_LOGIN);
		/* generate auto password */
		add_org.setPasswordAuto(Constants.PASSWORD_AUTO_GENERATION);
		/* setup a specific password if the auto generation is disabled */
		if (Constants.PASSWORD_AUTO_GENERATION == false) {
			add_org.setPassword(Constants.PASSWORD);
		}
		
		/* phone number: public number or extension number */
		add_org.setPhone(Constants.PHONE_NUMBER);
		/* fax */
		add_org.setFax(Constants.FAX);
		/* email */
		add_org.setEmail(Constants.EMAIL);
		/* address */
		add_org.setAddress(Constants.ADDRESS);
		/* city */
		add_org.setCity(Constants.CITY);
		/* postal code */
		add_org.setPcode(Constants.POSTAL_CODE);
		/* country code */
		add_org.setCountry(Constants.COUNTRY_CODE);
		/* interface language code */
		add_org.setInterfaceLang(Constants.INTERFACE_LANG_CODE);
		/* notes */
		add_org.setNotes(Constants.NOTES);
		/* notifyOnly */
		add_org.setNotifyOnly(Constants.NOTIFY_ONLY);
		/* scope */
		add_org.setScope(Constants.SCOPE_IDENTIFIER);
		/* deployment keeping unit */
		add_org.setLinkUUID(Constants.DEPLOYMENT_KEEPING_UNIT);
		
		AddOrganizationDocument aspd = AddOrganizationDocument.Factory.newInstance();
		aspd.setAddOrganization(add_org);
		/* make the call */
		try {
			/* the response */
			AddOrganizationResponseDocument response_document = stub.addOrganization(aspd, userCredentials);
			AddOrganizationResponse response = response_document.getAddOrganizationResponse();
			/* information from the response */
			System.out.println("Add Organization Account response:");
			System.out.println("\toperation status: " + response.getResult());
			if (response.getResult() ==  ResultDocument.Result.Enum.forString("success")) {
				System.out.println("\tThe following organization was created: id:" + response.getID() +
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

	/**
	 * Returns the service providers.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @return ServiceProviderList[] array with the service providers.
	 * @throws org.apache.axis2.AxisFault - when initializing the ServiceProvider stub.
	 */
	public final ServiceProviderList[] getServiceProviders(UserCredentialsDocument userCredentials) throws Exception {
		/* the ServiceProvider stub */
		ServiceProviderPortStub stub = new ServiceProviderPortStub(Constants.VOIPNOW_URL + "soap2/sp_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(stub);
		
		GetServiceProvidersDocument gspd = GetServiceProvidersDocument.Factory.newInstance();
		GetServiceProviders serviceProvidersRequest = GetServiceProviders.Factory.newInstance();
		gspd.setGetServiceProviders(serviceProvidersRequest);
		
		ServiceProviderList[] results = null;
		/* make the call */
		try {
			/* the response */
			GetServiceProvidersResponseDocument responseDocument = stub.getServiceProviders(gspd, userCredentials);
			GetServiceProvidersResponse response = responseDocument.getGetServiceProvidersResponse();
			results = response.getServiceProviderArray();
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
		
		return results;
	}
}