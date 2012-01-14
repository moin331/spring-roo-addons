package net.tzolov.roo.addon.oauth2.provider;

/**
 * @author Christian Tzolov (christian@tzolov.net)
 *
 */
public interface AuthorizationServerOperations {

	boolean isAuthorizationServerInstallationPossible();

	void installAuthorizationServer();

}