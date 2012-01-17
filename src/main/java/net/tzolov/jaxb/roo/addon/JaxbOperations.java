package net.tzolov.jaxb.roo.addon;

/**
 * Provides JAXB 2 installation services.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 */
public interface JaxbOperations {

	boolean isInstallJaxbSchemaCompilerAvailable();

	void installJaxbSchemaCompiler(String schemaDirectory,
			String generateDirectory);
}