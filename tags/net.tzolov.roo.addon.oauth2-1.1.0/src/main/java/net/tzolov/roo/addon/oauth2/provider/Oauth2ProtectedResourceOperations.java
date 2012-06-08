package net.tzolov.roo.addon.oauth2.provider;

/**
 * @author Christian Tzolov (christian@tzolov.net)
 * 
 */
public interface Oauth2ProtectedResourceOperations {

	boolean isOAuth2ProtectedResourceInstallationPossible();

	void installOAuth2ProtectedResource(String protectedResourceName, String urlPathAccess);
}