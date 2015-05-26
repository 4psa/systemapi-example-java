/*
 * 4PSA VoipNow - Common operations used in more than one test
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */

/*
 * In this file there are operations which are not related to a specific test:
 * * get the user credentials
 * * set the 'chunked' value of the 'transfer-coding' property for a http header used for a specified stub
 * * ping the VoipNow server
 * * get the charging plans created by the admin user
 * * get the charging plans created by a specified user
 */ 

 
package com._4psa.systemapi.demo.main;

import com._4psa.billingdata_xsd._3_0_0.ChargingPlanList;
import com._4psa.billingmessages_xsd._3_0_0.GetChargingPlansDocument;
import com._4psa.billingmessages_xsd._3_0_0.GetChargingPlansDocument.GetChargingPlans;
import com._4psa.billingmessages_xsd._3_0_0.GetChargingPlansResponseDocument;
import com._4psa.billingmessages_xsd._3_0_0.GetChargingPlansResponseDocument.GetChargingPlansResponse;
import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;
import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import com._4psa.pbxmessages_xsd._3_0_0.PingDocument;
import com._4psa.pbxmessages_xsd._3_0_0.PingResponseDocument.PingResponse;
import com._4psa.pbxmessages_xsd._3_0_0.PingResponseDocument;
import com._4psa.voipnowservice._3_0_0.BillingPortStub;
import com._4psa.voipnowservice._3_0_0.PBXPortStub;
import java.rmi.RemoteException;


/**
 * A class with operations used in more than one test.
 */
public class CommonOperations {
	/**
	 * This class should not be instantiated.
	 */
	private CommonOperations() {
		throw new AssertionError();
	}
	
	/**
	 * Prepares the credentials for a SOAP call.
	 * @return UserCredentialsDocument The credentials to be used in the SOAP call.
	 */
	public static UserCredentialsDocument getUserCredentials() {
		UserCredentialsDocument.UserCredentials cred = UserCredentialsDocument.UserCredentials.Factory.newInstance();
		cred.setAccessToken(Constants.ACCESS_TOKEN);
		UserCredentialsDocument userCredentials = UserCredentialsDocument.Factory.newInstance();
		userCredentials.setUserCredentials(cred);
		
		return userCredentials;
	}
	
	/**
	 * Sets the Chunked value of the http header used for a specified stub.
	 * @param stub The specified stub.
	 */
	public static void setChunked(Stub stub) {
		stub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
	}
	
	/**
	 * Makes a Ping call.
	 * @param UserCredentialsDocument cred - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the PBX stub.
	 */
	public static void ping(UserCredentialsDocument cred) throws Exception {
		/* initialize the PBX stub */
		PBXPortStub stub = new PBXPortStub(Constants.VOIPNOW_URL + "soap2/pbx_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(stub);
		
		PingDocument ping_req = PingDocument.Factory.newInstance();
		/* prepare the body of the request */
		ping_req.setPing("Hello_server");
		
		/* make the Ping call */
		try {
			/* the response */
			PingResponseDocument response_doc = stub.ping(ping_req, cred);
			PingResponse response = response_doc.getPingResponse();
			
			System.out.println("Ping response:");
			System.out.println("\tVersion: "+response.getVersion());
			System.out.println("\tInfrastructure ID: "+response.getInfrastructureID());
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
	}
	
	/**
	 * Returns the charging plans created by the admin user.
	 * @param UserCredentialsDocument cred - the user credentials.
	 * @return ChargingPlanList[] array with the charging plans.
	 * @throws org.apache.axis2.AxisFault - when initializing the Billing stub.
	 */
	public static ChargingPlanList[] getChargingPlans(UserCredentialsDocument cred) throws Exception {	
		/* the Billing stub */
		BillingPortStub stub = new BillingPortStub(Constants.VOIPNOW_URL + "soap2/billing_agent.php");
		
		/* fix Length error */
		stub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
		
		GetChargingPlansDocument gcpd = GetChargingPlansDocument.Factory.newInstance();
		GetChargingPlans chargingPlansRequest = GetChargingPlans.Factory.newInstance();
		gcpd.setGetChargingPlans(chargingPlansRequest);
		
		ChargingPlanList[] results = null;
		/* make the call */
		try {
			/* the response */
			GetChargingPlansResponseDocument responseDocument = stub.getChargingPlans(gcpd, cred);
			GetChargingPlansResponse response = responseDocument.getGetChargingPlansResponse();
			results = response.getChargingPlanArray();
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
		
		return results;
	}
	
	/**
	 * Returns the charging plans created by a specified user.
	 * @param int userId - the id of the user.
	 * @param UserCredentialsDocument cred - the user credentials.
	 * @return ChargingPlanList[] array with the charging plans.
	 * @throws org.apache.axis2.AxisFault - when initializing the Billing stub.
	 */
	public static ChargingPlanList[] getChargingPlans(int userID, UserCredentialsDocument cred) throws Exception {
		/* the Billing stub */
		BillingPortStub stub = new BillingPortStub(Constants.VOIPNOW_URL + "soap2/billing_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(stub);
		
		GetChargingPlansDocument gcpd = GetChargingPlansDocument.Factory.newInstance();        
		GetChargingPlans chargingPlansRequest = GetChargingPlans.Factory.newInstance();
		chargingPlansRequest.setUserID(userID);
		gcpd.setGetChargingPlans(chargingPlansRequest);        
		
		ChargingPlanList[] results = null;
		/* make the call */
		try {
			/* the response */
			GetChargingPlansResponseDocument responseDocument = stub.getChargingPlans(gcpd, cred);
			GetChargingPlansResponse response = responseDocument.getGetChargingPlansResponse();
			results = response.getChargingPlanArray();
		} catch (RemoteException ex) {
			System.out.println(ex);
		}
		
		return results;
	}    
}