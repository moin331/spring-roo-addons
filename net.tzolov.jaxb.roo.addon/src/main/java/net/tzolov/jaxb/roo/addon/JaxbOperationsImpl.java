package net.tzolov.jaxb.roo.addon;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Plugin;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * Provides JAXB 2 installation services.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 */
@Component
@Service
public class JaxbOperationsImpl implements JaxbOperations {

	@Reference
	private MetadataService metadataService;
	@Reference
	private ProjectOperations projectOperations;

	private ComponentContext context;

	protected void activate(ComponentContext context) {
		this.context = context;
	}

	public boolean isInstallJaxbSchemaCompilerAvailable() {

		ProjectMetadata project = (ProjectMetadata) metadataService
				.get(ProjectMetadata.getProjectIdentifier());

		if (project == null) {
			return false;
		}

		// only permit installation if they don't already have some version of
		// JAXB2 build plugin installed
		return project.getBuildPluginsExcludingVersion(
				new Plugin("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin",
						"0.7.4")).size() == 0;
	}

	public void installJaxbSchemaCompiler(String schemaDirectory,
			String generateDirectory) {

		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(getClass());

		// Add dependencies to POM
		updateDependencies(configuration);

		// Add repository to POM
		updateMavenRepository(configuration);

		// Add plugin repository to POM
		updateMavenPluginRepository(configuration);

		// Add build plugins to POM
		updateBuildPlugins(configuration, schemaDirectory, generateDirectory);
	}

	private boolean hasText(String text) {

		if (null == text)
			return false;

		return (text.length() > 0);
	}

	private void updateDependencies(Element configuration) {
		List<Element> databaseDependencies = XmlUtils.findElements(
				"/configuration/jaxb/dependencies/dependency", configuration);

		for (Element dependencyElement : databaseDependencies) {
			projectOperations.addDependency(new Dependency(dependencyElement));
		}
	}

	private void updateBuildPlugins(Element configuration,
			String schemaDirectory, String generateDirectory) {

		List<Element> databasePlugins = XmlUtils.findElements(
				"/configuration/jaxb/build/plugins/plugin", configuration);

		for (Element pluginElement : databasePlugins) {
			Plugin buildPlugin = new Plugin(pluginElement);
			
			if ((hasText(generateDirectory) || hasText(schemaDirectory))
					&& buildPlugin.getArtifactId().equals("maven-jaxb2-plugin")) {
				
				Element pluginConfigurationElement = buildPlugin
						.getConfiguration().getConfiguration();
				
				if (hasText(schemaDirectory)) {
					XmlUtils.findFirstElement("schemaDirectory",
							pluginConfigurationElement).setTextContent(
							schemaDirectory);
				}

				if (hasText(generateDirectory)) {
					XmlUtils.findFirstElement("generateDirectory",
							pluginConfigurationElement).setTextContent(
							generateDirectory);
				}

			}
			
			System.out.println("update plugins:" + buildPlugin.toString());

			projectOperations.updateBuildPlugin(buildPlugin);
		}
	}

	private void updateMavenRepository(Element configuration) {

		List<Element> databaseRepostories = XmlUtils.findElements(
				"/configuration/jaxb/repositories/repository", configuration);

		for (Element repositoryElement : databaseRepostories) {
			projectOperations.addRepository(new Repository(repositoryElement));
		}
	}

	private void updateMavenPluginRepository(Element configuration) {

		List<Element> databasePluginRepostories = XmlUtils.findElements(
				"/configuration/jaxb/pluginRepositories/pluginRepository",
				configuration);

		for (Element pluginRepositoryElement : databasePluginRepostories) {
			projectOperations.addPluginRepository(new Repository(
					pluginRepositoryElement));
		}
	}
}
