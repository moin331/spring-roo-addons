package net.tzolov.jetty.executable.war.roo.addon;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.Plugin;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of commands that are available via the Roo shell.
 * 
 * @since 1.1.0M1
 */
@Component
@Service
public class ExecutableWarOperationsImpl implements ExecutableWarOperations {

	private static Logger logger = Logger
			.getLogger(ExecutableWarOperations.class.getName());

	@Reference
	private FileManager fileManager;
	@Reference
	private MetadataService metadataService;
	@Reference
	private ProjectOperations projectOperations;
	@Reference
	private PathResolver pathResolver;

	private ComponentContext context;

	protected void activate(ComponentContext context) {
		this.context = context;
	}

	public boolean isExecutableWarAvailible() {
		return true;
		// return metadataService.get(ProjectMetadata.getProjectIdentifier()) !=
		// null;
	}

	public void setupExecutableWar() {

		// Parse the configuration.xml file
		Element configurationXml = XmlUtils.getConfiguration(getClass());

		// Add dependencies to POM
		updateDependencies(configurationXml);

		// Create an assembly plugin or update its configuration
		updateAssemblyPlugin(configurationXml);

		// Copy the execwar.xml template
		copyTemplate(Path.SRC_MAIN_RESOURCES, "execwar.xml",
				"execwar-template.xml");

		// Copy the Start.java template
		copyTemplate(Path.SRC_MAIN_JAVA, "Start.java", "Start-template.java");
	}

	private void copyTemplate(Path path, String targetName, String templateName) {

		String execWarDestination = pathResolver
				.getIdentifier(path, targetName);

		if (!fileManager.exists(execWarDestination)) {
			try {
				FileCopyUtils.copy(TemplateUtils.getTemplate(getClass(),
						templateName),
						fileManager.createFile(execWarDestination)
								.getOutputStream());
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}

		logger.info(targetName + "added to" + path);

	}

	private void updateDependencies(Element configuration) {

		List<Element> databaseDependencies = XmlUtils
				.findElements("/configuration/execwar/dependencies/dependency",
						configuration);

		for (Element dependencyElement : databaseDependencies) {
			projectOperations
					.dependencyUpdate(new Dependency(dependencyElement));
		}
	}

	private void updateAssemblyPlugin(Element configurationXml) {

		Element assemblyPluginElement = XmlUtils.findFirstElement(
				"//plugin[artifactId='maven-assembly-plugin']",
				configurationXml);

		Assert.notNull(assemblyPluginElement);

		Plugin assemblyPlugin = new Plugin(assemblyPluginElement);
		logger.info("Assembly plugin:" + assemblyPlugin.toString());

		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService
				.get(ProjectMetadata.getProjectIdentifier());
		Assert.notNull(projectMetadata, "Project metadata unavailable");

		if (projectMetadata.getBuildPluginsExcludingVersion(assemblyPlugin)
				.isEmpty()) {

			// Add assembly plugin to the POM
			projectOperations.buildPluginUpdate(assemblyPlugin);

		} else {

			// Assembly plugin already exists. Update its configuration
			updateAssemblyPluginConfiguration();
		}
	}

	private void updateAssemblyPluginConfiguration() {

		String pom = pathResolver.getIdentifier(Path.ROOT, "/pom.xml");

		MutableFile mutableFile = fileManager.updateFile(pom);

		try {
			Document document = XmlUtils.getDocumentBuilder().parse(
					mutableFile.getInputStream());

			Element pluginConfiguration = XmlUtils
					.findFirstElement(
							"//plugin[artifactId='maven-assembly-plugin']/configuration",
							(Element) document.getFirstChild());

			Assert.notNull(pluginConfiguration,
					"plugin[artifactId='maven-assembly-plugin']/configuration unable to be found");

			// Add schemaDirectory if set
			Element finalName = document.createElement("finalName");
			finalName.setTextContent("${artifactId}-${version}");
			pluginConfiguration.appendChild(finalName);

			Element appendAssemblyId = document
					.createElement("appendAssemblyId");
			appendAssemblyId.setTextContent("true");
			pluginConfiguration.appendChild(appendAssemblyId);

			Element useProjectArtifact = document
					.createElement("useProjectArtifact");
			useProjectArtifact.setTextContent("false");
			pluginConfiguration.appendChild(useProjectArtifact);

			Element outputDirectory = document.createElement("outputDirectory");
			outputDirectory.setTextContent("target");
			pluginConfiguration.appendChild(useProjectArtifact);

			Element descriptors = document.createElement("descriptors");
			Element descriptor = document.createElement("descriptor");
			descriptor.setTextContent("src/main/resources/execwar.xml");
			descriptors.appendChild(descriptor);
			pluginConfiguration.appendChild(descriptors);

			Element archive = document.createElement("archive");
			Element manifest = document.createElement("manifest");
			Element mainClass = document.createElement("mainClass");
			mainClass.setTextContent("Start");
			manifest.appendChild(mainClass);
			archive.appendChild(manifest);
			pluginConfiguration.appendChild(archive);

			XmlUtils.writeXml(mutableFile.getOutputStream(), document);

		} catch (Exception ex) {
			throw new IllegalStateException("Could not open POM '" + pom + "'",
					ex);
		}
	}
}