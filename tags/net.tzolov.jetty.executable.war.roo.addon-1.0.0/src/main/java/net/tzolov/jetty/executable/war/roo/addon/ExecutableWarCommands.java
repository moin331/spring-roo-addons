package net.tzolov.jetty.executable.war.roo.addon;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CommandMarker;

/**
 */
@Component
@Service
public class ExecutableWarCommands implements CommandMarker {

	private static Logger logger = Logger.getLogger(ExecutableWarCommands.class
			.getName());

	@Reference
	private ExecutableWarOperations operations;

	@CliAvailabilityIndicator("executable war setup")
	public boolean isExecutableWarAllowed() {
		return operations.isExecutableWarAvailible();
	}

	@CliCommand(value = "executable war setup", help = "Creates an <XXX>-EXECWAR.war executable application. Usage: java -jar <XXX>-EXECWAR.war")
	public void setupExecutableWar() {
		operations.setupExecutableWar();
	}
}