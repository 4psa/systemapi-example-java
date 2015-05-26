/*
 * 4PSA VoipNow - Java SOAP Client: Constants
 *
 * Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 *
 */

/*
 * In this file there are some constants used for creating the enities.
 * The ones set to "CHANGEME" must be changed for the tests to work.
 */
 
 
package com._4psa.systemapi.demo.main;


/**
 * Useful constants
 */
public class Constants {
	/* VoipNow server URL - can be http or https (example: http://my.server.com/)*/
	public static final String VOIPNOW_URL = "CHANGEME";
	/* Credentials for connecting to VoipNow */
	public static final String ACCESS_TOKEN = "CHANGEME";
	
	/* Entity information */
	public static final String COMPANY = "4PSA";
	public static final String NAME = "John Smith_" + (int) (Math.random() * 1000000);
	public static final String SERVICE_PROVIDER_LOGIN = "John_Smith_" + (int) (Math.random() * 1000000);
	public static final String ORGANIZATION_LOGIN = "John_Smith_" + (int) (Math.random() * 1000000);
	public static final String USER_LOGIN = "John_Smith_" + (int) (Math.random() * 1000000);
	public static final String EXTENSION_LABEL = "John_Smith_" + (int) (Math.random() * 1000000);
	
	/* Autogenerate password */
	public static final boolean PASSWORD_AUTO_GENERATION = true;
	/* Or use your own password */
	public static final String PASSWORD = "this_is_a_nice_password";
	
	public static final String PHONE_NUMBER = "0003*" + ((int) (Math.random() * 900) + 100);
	public static final String FAX = "33775";
	public static final String EMAIL = "somebody@example.com";
	public static final String ADDRESS = "Street Mihai Viteazul no. 12";
	public static final String CITY = "Bucharest";
	public static final String POSTAL_CODE = "12985";
	public static final String COUNTRY_CODE = "RO";
	public static final String INTERFACE_LANG_CODE = "RO";
	public static final String NOTES = "I have created this entity with a SOAP client written in Java";
	public static final String NOTIFY_ONLY = "0";
	public static final String SCOPE_IDENTIFIER = "scopeIdentifier";
	public static final String DEPLOYMENT_KEEPING_UNIT = "DeploymentKeepingUnit";
	/* The extension type.
	 * Must have one of the following values:
	 * "term"
	 * "phoneQueue"
	 * "ivr"
	 * "voicecenter"
	 * "conference"
	 * "callback"
	 * "intercom"
	 * "queuecenter"
	 */
	public static final String EXTENSION_TYPE = "term";
}