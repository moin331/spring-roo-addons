package net.tzolov.jaxb.roo.addon;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Commands for the Jaxb2 add-on to be used by the ROO shell.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 * 
 */
@Component
@Service
public class JaxbCommands implements CommandMarker {

	private static Logger logger = Logger.getLogger(JaxbCommands.class
			.getName());

	@Reference  private JaxbOperations jaxbOperations;

	protected void activate(ComponentContext context) {	    
    }

	protected void deactivate(ComponentContext context) {
	}
	
	@CliAvailabilityIndicator("jaxb xsd compiler setup")
	public boolean isInstallJaxbSchemaCompilerAvailable() {
		return jaxbOperations.isInstallJaxbSchemaCompilerAvailable();
	}

	@CliCommand(value = "jaxb xsd compiler setup", help = "Install JAXB2 maven plugin to generate Java classes from an XML schema with the xjc schema compiler tool.")
	public void instalJaxbSchemaCompiler(
			@CliOption(key = { "generateDirectory" }, mandatory = false, help = "Target directory for the generated code, target/generated-sources/xjc by default.", specifiedDefaultValue = "target/generated-sources/xjc") String generateDirectory,
			@CliOption(key = { "schemaDirectory" }, mandatory = false, help = "Specifies the schema directory, src/main/resources by default", specifiedDefaultValue = "src/main/resources") String schemaDirectory) {

		jaxbOperations.installJaxbSchemaCompiler(schemaDirectory,
				generateDirectory);
	}
}