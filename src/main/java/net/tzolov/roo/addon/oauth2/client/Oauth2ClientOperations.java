package net.tzolov.roo.addon.oauth2.client;

/**
 * Christian Tzolov (christian@tzolov.net)
 *
 */
public interface Oauth2ClientOperations {

	String SECURITY_FILTER_NAME = "springSecurityFilterChain";

	boolean isOAuthClientInstallationPossible();

	void installOAuthClient(String authorizationServerUri, String protectedResourceUri);
}