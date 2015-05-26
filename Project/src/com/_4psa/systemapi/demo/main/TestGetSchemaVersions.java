/*
 * 4PSA VoipNow - Java SOAP User: Get schema versions
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */

/*
 * This file contains the test for getting the schema versions from the server.
 */ 

 
package com._4psa.systemapi.demo.main;

import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;
import com._4psa.pbxmessages_xsd._3_0_0.GetSchemaVersionsDocument;
import com._4psa.pbxmessages_xsd._3_0_0.GetSchemaVersionsResponseDocument;
import com._4psa.pbxmessages_xsd._3_0_0.GetSchemaVersionsResponseDocument.GetSchemaVersionsResponse;
import com._4psa.voipnowservice._3_0_0.PBXPortStub;
import java.rmi.RemoteException;


/**
 * Test for getting the versions of the schemas
 */
public class TestGetSchemaVersions {
	/**
	 * Creates a test which gets the schema versions from the server.
	 * @param UserCredentialsDocument userCredentials - the user credentials.
	 * @throws org.apache.axis2.AxisFault - when initializing the PBX stub.
	 */
	public TestGetSchemaVersions(UserCredentialsDocument userCredentials) throws Exception {
		/* the PBX stub */
		PBXPortStub stub = new PBXPortStub(Constants.VOIPNOW_URL + "soap2/pbx_agent.php");
		
		/* the http header used for the stub must have chunked transfer coding */
		CommonOperations.setChunked(stub);
		
		/* gets the schema versions */
		this.getInfo(stub, userCredentials);
	}
	
	/**
	 * Gets the versions of the schemas
	 * @param PBXPortStub stub - the PBX stub
	 * @param UserCredentialsDocument userCredentials - the user credentials
	 */
	public final void getInfo(PBXPortStub stub, UserCredentialsDocument userCredentials) {
		/* request object */
		GetSchemaVersionsDocument gsvd = GetSchemaVersionsDocument.Factory.newInstance();
		/* the String given as a parameter */
		gsvd.setGetSchemaVersions(userCredentials.toString());
		
		try {
			/* the response */
			GetSchemaVersionsResponseDocument responseDocument = stub.getSchemaVersions(gsvd, userCredentials);
			GetSchemaVersionsResponse response = responseDocument.getGetSchemaVersionsResponse();
			String[] results = response.getVersionsArray();
			System.out.println("Schema versions:");
			for (int i = 0; i < results.length; i++) {
				System.out.println("\t" + results[i]);
			}
		} catch (RemoteException ex) {
			ex.printStackTrace(System.out);
		}
	}
}