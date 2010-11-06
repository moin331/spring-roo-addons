package net.tzolov.jetty.executable.war.roo.addon;

/**
 * Interface of commands that are available via the Roo shell.
 *
 * @since 1.1.0-M1
 */
public interface ExecutableWarOperations {

	boolean isExecutableWarAvailible();

	void setupExecutableWar();
}