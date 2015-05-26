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

import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;


public final class NaiveTrustProvider extends Provider
{
	/* The name of the algorithm */
	private static final String TRUST_PROVIDER_ALG = "NaiveTrustAlgorithm";

	
	/* Need to refer to ourselves somehow to know if we're already registered */
	private static final String TRUST_PROVIDER_ID  = "NaiveTrustProvider";

	public NaiveTrustProvider() {
		super(TRUST_PROVIDER_ID, 
				(double) 0.1,
				"NaiveTrustProvider (provides all secure socket factories by ignoring problems in the chain of certificate trust)"
			);

		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				put("TrustManagerFactory." + NaiveTrustManagerFactory.getAlgorithm(),
					NaiveTrustManagerFactory.class.getName()
				);
				return null;
			}
		});
	}

	/**
	 * Trusts all certificates
	 * @param enableNaiveTrustProvider set to true to trust all certificates
	 */

	public static void setAlwaysTrust(boolean enableNaiveTrustProvider) {
		if (enableNaiveTrustProvider) {
			Provider registered = Security.getProvider (TRUST_PROVIDER_ID);
			if (null == registered) {
				Security.insertProviderAt (new NaiveTrustProvider (), 1);
				Security.setProperty ("ssl.TrustManagerFactory.algorithm", TRUST_PROVIDER_ALG);
			}
		} else {
			throw new UnsupportedOperationException("Disable Naive trust provider not yet implemented");
		}
	}

	public final static class NaiveTrustManagerFactory extends TrustManagerFactorySpi {
		public NaiveTrustManagerFactory() { }
		
		protected void engineInit(ManagerFactoryParameters mgrparams) {
		}
		
		protected void engineInit(KeyStore keystore) {
		}
		
		/**
		 * Returns a collection of trust managers that are naive.
		 * This collection is just a single element array containing NaiveTrustManager class
		 */
		protected TrustManager[] engineGetTrustManagers () {
			// Returns a new array of just a single NaiveTrustManager.
			return new TrustManager[] { new NaiveTrustManager()  };
		}

		/**
		 * Returns our "NaiveTrustAlgorithm" string.
		 * @return The string, "NaiveTrustAlgorithm"
		 */
		public static String getAlgorithm() {
			return TRUST_PROVIDER_ALG;
		}
	}
}