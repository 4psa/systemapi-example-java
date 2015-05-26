/*
 * 4PSA VoipNow - Java SOAP Client
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */

/*
 * This file contains the main method: initializes all the tests implemented in this example.
 */
 
 
package com._4psa.systemapi.demo.main;

import com._4psa.headerdata_xsd._3_0_0.UserCredentialsDocument;


/**
 * A suites of tests:
 * * ping the server
 * * get the schema versions from the server
 * * create a Service Provider account
 * * create an Organization account
 * * create an User account
 * * create an Extension account
 */
public class Main {
	/**
	 * @param args the command line arguments
	 * @throws java.lang.Exception - the exceptions thrown by the tests.
	 */
	public static void main(String[] args) throws Exception {
		/* trust all certificates */
		NaiveTrustProvider.setAlwaysTrust(true);

		/* create the user credentials */
		UserCredentialsDocument userCredentials = CommonOperations.getUserCredentials();
		
		/* ping the VoipNow server */
		CommonOperations.ping(userCredentials);
		
		/* fetch all supported schema versions */
		TestGetSchemaVersions schemaVersions = new TestGetSchemaVersions(userCredentials);
	
		/* create a new service provider account */
		TestAddServiceProviderAccount sp = new TestAddServiceProviderAccount(userCredentials);

		/* create a new organization account */
		TestAddOrganizationAccount org = new TestAddOrganizationAccount(userCredentials);

		/* create a new user account */
		TestAddUserAccount user = new TestAddUserAccount(userCredentials);

		/* create a new extension account */
		TestAddExtensionAccount extension = new TestAddExtensionAccount(userCredentials);
	}
}