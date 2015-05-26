/*
 * 4PSA VoipNow - Class for accepting all the certificates.
 *
 * Code inspired from http://creativecommons.org/licenses/by/3.0/
 *
 */
 
/*
 * This file contains a class used for accepting all the certificates.
 */
 
 
package com._4psa.systemapi.demo.main;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;


/**
 * Accepts all certificates
 */
public class NaiveTrustManager implements X509TrustManager
{
	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 */
	public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
	}

	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 */
	public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}