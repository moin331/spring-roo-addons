package net.tzolov.roo.addon.oauth2.provider;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Commands for the security oauth2 add-on to be used by the ROO shell.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 *
 */
@Component
@Service
public class Oauth2Commands implements CommandMarker {

	// Fields
	@Reference private AuthorizationAndResourceServerOperations providerAndResourceInMemoryOperations;
	@Reference private Oauth2ProtectedResourceOperations protectedResourceOperations;
	@Reference private AuthorizationServerOperations authorizeServerOperations;
		
	//
	// OAUTH2 PROVIDER (AUTHORIZATION SERVER)
	//
	@CliAvailabilityIndicator(value = "oauth2 authorization-server setup")
	public boolean isInstallOAuth2AuthorizeServerAvailable() {
		return authorizeServerOperations.isAuthorizationServerInstallationPossible();
	}

	@CliCommand(value = "oauth2 authorization-server setup", help = "Install Spring Security OAuth2 Authorization Server. Requires 'web mvc setup' addon to be installed first")
	public void installOAuth2AuthorizeServer() {
		authorizeServerOperations.installAuthorizationServer();
	}
	
	//
	// OAUTH2 PROTECTED RESOURCES (RESOURCE SERVER)
	//
	@CliAvailabilityIndicator(value = "oauth2 resource setup")
	public boolean isInstallOAuthResourceAvailable() {
		return protectedResourceOperations.isOAuth2ProtectedResourceInstallationPossible();
	}
	
	@CliCommand(value = "oauth2 resource setup", help = "Add new OAuth2 protected resource to your project. Requires the 'web mvc steup' and 'security setup' addons to be installed first")
	public void installOAuthResource(
			@CliOption(key = { "protectedResourceName" }, mandatory = true, help = "resource URI path and controller name") String protectedResourceName,
			@CliOption(key = { "urlPathAccess" }, mandatory = false, help = "The access configuration attributes that apply for the configured path. (note: that the access format differs for expression based and basic http configuration)") String urlPathAccess) {
		protectedResourceOperations.installOAuth2ProtectedResource(protectedResourceName, urlPathAccess);
	}

	//
	// Collocated Authorization and Resource Servers sharing a single in memory TokenStore
	//
	@CliAvailabilityIndicator(value = "oauth2 collocated-authorization-and-resource-server setup")
	public boolean isInstallOAuthProviderAvailable() {
		return providerAndResourceInMemoryOperations.isSecurityInstallationPossible();
	}

	@CliCommand(value = "oauth2 collocated-authorization-and-resource-server setup", help = "Install Spring Security OAuth2 Provider together with a Protected Resource (shared in-memory TokenStore)")
	public void installOAuthProvider() {
		providerAndResourceInMemoryOperations.installOAuth2Security();
	}
}