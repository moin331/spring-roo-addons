package net.tzolov.roo.addon.oauth2.client;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Commands for the oauth2 client add-on to be used by the ROO shell.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 * 
 */
@Component
@Service
public class Oauth2ClientCommands implements CommandMarker {

	// Fields
	@Reference
	private Oauth2ClientOperations ouath2ClientOperations;

	@CliAvailabilityIndicator("oauth2 web-client setup")
	public boolean isInstallOAuthClientAvailable() {
		return ouath2ClientOperations.isOAuthClientInstallationPossible();
	}

	@CliCommand(value = "oauth2 web-client setup", help = "Install Spring Security OAuth2 Client into your project")
	public void installOAuthClient(
			@CliOption(key = { "authorizationServerUri" }, mandatory = true) String authorizationServerUri,
			@CliOption(key = { "protectedResourceUri" }, mandatory = true) String protectedResourceUri) {
		
		ouath2ClientOperations.installOAuthClient(authorizationServerUri, protectedResourceUri);
	}
}