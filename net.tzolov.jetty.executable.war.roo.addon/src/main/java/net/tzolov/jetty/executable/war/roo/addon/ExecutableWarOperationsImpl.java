package net.tzolov.jetty.executable.war.roo.addon;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.Plugin;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.FileUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * Implementation of commands that are available via the Roo shell.
 * 
 * @since 1.1.0M1
 */
@Component
@Service
public class ExecutableWarOperationsImpl implements ExecutableWarOperations {

	private static final String EXECWAR_TEMPLATE_XML = "execwar-template.xml";

	private static final String START_TEMPLATE_JAVA = "ExecWar-template.java";

	private static final String EXECWAR_XML = "execwar.xml";

	private static final String START_JAVA = "ExecWar.java";

	private static Logger logger = Logger.getLogger(ExecutableWarOperations.class.getName());

	@Reference
	private FileManager fileManager;
	@Reference
	private ProjectOperations projectOperations;
	@Reference
	private PathResolver pathResolver;

	private ComponentContext context;

	protected void activate(ComponentContext context) {
		this.context = context;
	}

	public boolean isExecutableWarAvailible() {

		// only permit installation if not applied so far
		return projectOperations.isFocusedProjectAvailable() 
				&& !fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_JAVA, getTopLevelPackageFilePath() + "/" + START_JAVA));
	}

	public void setupExecutableWar() {

		logger.info("Install Executable War!");

		projectOperations.removeBuildPlugin(projectOperations.getFocusedModuleName(), new Plugin("org.apache.maven.plugins", "maven-assembly-plugin", "2.2.1"));
		
		// Parse the configuration.xml file
		Element configurationXml = XmlUtils.getConfiguration(getClass());

		// Add dependencies to POM
		updateDependencies(configurationXml);
		
		// Update plugins from the configuration file (note: only plugin's definition is performed here. The configuration setup is performed
		// below
		updateBuildPlugins(configurationXml);

		// Create an assembly plugin or update its configuration
		// updateAssemblyPluginConfiguration();

		// Copy the execwar.xml template
		copyTemplate(Path.SRC_MAIN_RESOURCES, EXECWAR_XML, EXECWAR_TEMPLATE_XML);

		// Copy the Start.java template
		copyTemplate(Path.SRC_MAIN_JAVA, getTopLevelPackageFilePath() + "/" + START_JAVA, START_TEMPLATE_JAVA);
	}

	private String getTopLevelPackageFilePath() {
		return projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName().replace(".", "/");
	}

	private void copyTemplate(Path path, String targetName, String templateName) {

		String execWarDestination = pathResolver.getFocusedIdentifier(path, targetName);

		if (!fileManager.exists(execWarDestination)) {
			try {
				String input = FileCopyUtils.copyToString(new InputStreamReader(FileUtils.getInputStream(getClass(), templateName)));

				input = input.replace("__TOP_LEVEL_PACKAGE__", projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName());
				input = input.replace("__PROJECT_NAME__", projectOperations.getFocusedProjectName());

				FileCopyUtils.copy(input.getBytes(), fileManager.createFile(execWarDestination).getOutputStream());
				
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}
	}

	private void updateDependencies(Element configuration) {

		List<Element> xmlDependencies = XmlUtils.findElements("/configuration/execwar/dependencies/dependency", configuration);

		for (Element xmlDependecy : xmlDependencies) {
			projectOperations.addDependency(projectOperations.getFocusedModuleName(), new Dependency(xmlDependecy));
		}
	}

	private void updateBuildPlugins(Element configuration) {

		List<Element> xmlPlugins = XmlUtils.findElements("/configuration/execwar/build/plugins/plugin", configuration);

		logger.info("Update build plugins:" +xmlPlugins.size() );
		for (Element xmlPlugin : xmlPlugins) {
			Plugin buildPlugin = new Plugin(xmlPlugin);

			if (buildPlugin.getArtifactId().equals("maven-assembly-plugin")) {
				
				logger.info("Update maven-assembly-plugin build plugins");
				
				projectOperations.removeBuildPlugin(projectOperations.getFocusedModuleName(), buildPlugin);
				
				Element conf = buildPlugin.getConfiguration().getConfiguration();
				Element mainClass = XmlUtils.findFirstElement("archive/manifest/mainClass", conf);
				
				Assert.notNull(mainClass,  "plugin[artifactId='maven-assembly-plugin']/configuration/archive/manifest/mainClass unable to be found");
				
				JavaPackage topLevelPackage = projectOperations.getFocusedTopLevelPackage();

				mainClass.setTextContent(topLevelPackage.getFullyQualifiedPackageName() + ".ExecWar");
			}

			projectOperations.updateBuildPlugin(projectOperations.getFocusedModuleName(), buildPlugin);
		}
	}
}