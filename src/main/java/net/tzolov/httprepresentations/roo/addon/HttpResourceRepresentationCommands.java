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
public class HttpResourceRepresentationCommands implements CommandMarker {

	@Reference  private HttpResourceRepresentationOperations contentResolovernOperations;

	@CliAvailabilityIndicator("http resource representation setup")
	public boolean isInstallHttpResourceRepresentationAvailable() {
		return contentResolovernOperations.isInstallHttpResourceRepresentationAvailable();
	}

	@CliCommand(value = "http resource representation setup", help = "Install HTTP Content Negotiation Resolver into your project")
	public void installHttpResourceRepresentation() {
		contentResolovernOperations.installHttpResourceRepresentation();
	}

	@CliAvailabilityIndicator("http resource add oxm")
	public boolean isInstallOxmBindingAvailable() {

		return contentResolovernOperations.isInstallContentOxmBinding();
	}

	@CliCommand(value = "http resource add oxm", help = "Define a class for object to xml mapping response")
	public void installOxmBinding(
			@CliOption(key = "class", mandatory = true, help = "The class to map for xml marshaling") JavaType typeName) {

		contentResolovernOperations.installContentOxmBinding(typeName);
	}
}