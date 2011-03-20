package net.tzolov.httprepresentations.roo.addon;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.support.logging.HandlerUtils;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides web content resolver services.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 */
@Component
@Service
public class HttpResourceRepresentationOperationsImpl implements HttpResourceRepresentationOperations {

	private static final Logger logger = HandlerUtils
			.getLogger(HttpResourceRepresentationOperationsImpl.class);

	private static final Dependency DEPENDENCY_JACKSON_JAXRS = new Dependency(
			"org.codehaus.jackson", "jackson-jaxrs", "1.4.1");

	private static final String APPLICATION_CONTEXT_CONTENTRESOLVER_XML = "applicationContext-contentresolver.xml";

	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private MetadataService metadataService;
	@Reference private ProjectOperations projectOperations;
	@Reference private PhysicalTypeMetadataProvider physicalTypeMetadataProvider;

	public boolean isInstallHttpResourceRepresentationAvailable() {

		ProjectMetadata project = (ProjectMetadata) metadataService
				.get(ProjectMetadata.getProjectIdentifier());

		if (project == null) {
			return false;
		}

		// only permit installation if not applied so far
		if (fileManager.exists(pathResolver.getIdentifier(
				Path.SPRING_CONFIG_ROOT,
				APPLICATION_CONTEXT_CONTENTRESOLVER_XML))) {

			return false;
		}

		// only permit installation if they don't already have some version of
		return project
				.getDependenciesExcludingVersion(DEPENDENCY_JACKSON_JAXRS)
				.size() == 0;
	}

	public void installHttpResourceRepresentation() {

		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(getClass());

		// Add dependencies to POM
		updateDependencies(configuration);

		// copy the template across
		String destination = pathResolver.getIdentifier(
				Path.SPRING_CONFIG_ROOT,
				APPLICATION_CONTEXT_CONTENTRESOLVER_XML);

		if (!fileManager.exists(destination)) {
			try {
				FileCopyUtils.copy(TemplateUtils.getTemplate(getClass(),
						"applicationContext-contentresolver-template.xml"),
						fileManager.createFile(destination).getOutputStream());
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}
	}

	public boolean isInstallContentOxmBinding() {

		ProjectMetadata project = (ProjectMetadata) metadataService
				.get(ProjectMetadata.getProjectIdentifier());

		if (project == null) {
			return false;
		}

		// only permit if the HttpResourceRepresentation is already setup
		return fileManager.exists(pathResolver.getIdentifier(
				Path.SPRING_CONFIG_ROOT,
				APPLICATION_CONTEXT_CONTENTRESOLVER_XML));
	}

	public void installContentOxmBinding(JavaType typeName) {
		Assert.notNull(typeName, "Java type required");

		String id = physicalTypeMetadataProvider.findIdentifier(typeName);
		if (id == null) {
			logger.warning("Cannot locate source for '"
					+ typeName.getFullyQualifiedTypeName() + "'");
			return;
		}

		String contentresolver = pathResolver.getIdentifier(
				Path.SPRING_CONFIG_ROOT,
				APPLICATION_CONTEXT_CONTENTRESOLVER_XML);

		MutableFile mutableFile = fileManager.updateFile(contentresolver);

		Document document;
		try {
			document = XmlUtils.getDocumentBuilder().parse(
					mutableFile.getInputStream());
		} catch (Exception ex) {
			throw new IllegalStateException("Could not open '"
					+ contentresolver + "'", ex);
		}

		Element rootElement = (Element) document.getFirstChild();

		// Element oxmJaxbMarshaller =
		// XmlUtils.findFirstElement("//*[namespace-uri()='http://www.springframework.org/schema/oxm' and @id='jaxbMarshaller']",
		// rootElement);

		Element oxmJaxbMarshaller = XmlUtils.findFirstElement(
				"/beans/jaxb2-marshaller", rootElement);
		logger.warning("oxmJaxbMarshaller = " + oxmJaxbMarshaller);
		Assert.notNull(oxmJaxbMarshaller,
				"/beans/jaxb2-marshaller definition unable to be found");

		Element oxmBinding = document.createElement("oxm:class-to-be-bound");
		oxmBinding.setAttribute("name", typeName.getFullyQualifiedTypeName());

		oxmJaxbMarshaller.appendChild(oxmBinding);

		XmlUtils.writeXml(mutableFile.getOutputStream(), document);
	}
	
	private void updateDependencies(Element configuration) {
		List<Element> databaseDependencies = XmlUtils.findElements(
				"/configuration/httprepresentations/dependencies/dependency", configuration);

		for (Element dependencyElement : databaseDependencies) {
			projectOperations
					.addDependency(new Dependency(dependencyElement));
		}
	}

}
