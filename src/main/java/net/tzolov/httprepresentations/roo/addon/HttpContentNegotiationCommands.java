package net.tzolov.httprepresentations.roo.addon;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Commands for the content resolver add-on to be used by the ROO shell.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 * 
 */
@Component
@Service
public class HttpContentNegotiationCommands implements CommandMarker {

	@Reference
	private HttpContentNegotiationOperations contentNegotiationOperations;

	@CliAvailabilityIndicator("http content-negotiation setup")
	public boolean isInstallHttpResourceRepresentationAvailable() {
		return contentNegotiationOperations.isInstallHttpContentNegotiationAvailable();
	}

	@CliCommand(value = "http content-negotiation setup", help = "Install JSON & JSONP  HTTP Content Negotiation Resolver into your project")
	public void installHttpResourceRepresentation() {
		contentNegotiationOperations.installHttpContentNegotiation();
	}

	@CliAvailabilityIndicator("http content-negotiation add oxm")
	public boolean isInstallOxmBindingAvailable() {
		return contentNegotiationOperations.isInstallContentOxmBinding();
	}

	@CliCommand(value = "http content-negotiation add oxm", help = "Define a class for object to xml mapping response. The first time it also installs the Jaxb2 resolver")
	public void installOxmBinding(
			@CliOption(key = "class", mandatory = true, help = "The class to map for xml marshaling") JavaType typeName) {
		contentNegotiationOperations.installContentOxmBinding(typeName);
	}

	@CliAvailabilityIndicator("http cross-origin-resource-sharing setup")
	public boolean isInstallCORS() {
		return contentNegotiationOperations.isInstallCORS();
	}

	@CliCommand(value = "http cross-origin-resource-sharing setup", help = "Install Cross-Origin Resource Sharing")
	public void installCORS() {
		contentNegotiationOperations.installCORS();
	}

}