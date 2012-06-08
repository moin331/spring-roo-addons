package net.tzolov.roo.addon.oauth2.provider;

/**
 * @author Christian Tzolov (christian@tzolov.net)
 *
 */
public interface AuthorizationAndResourceServerOperations {

	String SECURITY_FILTER_NAME = "springSecurityFilterChain";

	boolean isSecurityInstallationPossible();

	void installOAuth2Security();
}