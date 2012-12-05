package net.tzolov.httprepresentations.roo.addon;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.TypeLocationService;
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
import org.springframework.roo.support.util.FileUtils;
import org.springframework.roo.support.util.WebXmlUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides json web content resolver services.
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 */
@Component
@Service
public class HttpContentNegotiationOperationsImpl implements HttpContentNegotiationOperations {

	private static final Logger logger = HandlerUtils.getLogger(HttpContentNegotiationOperationsImpl.class);

	private static final String APPLICATION_CONTEXT_CONTENTRESOLVER_XML = "applicationContext-contentresolver.xml";

	@Reference
	private FileManager fileManager;
	@Reference
	private PathResolver pathResolver;
	@Reference
	private MetadataService metadataService;
	@Reference
	private ProjectOperations projectOperations;
	@Reference
	private TypeLocationService typeLocationService;

	public boolean isInstallHttpContentNegotiationAvailable() {

		ProjectMetadata project = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier(projectOperations
				.getFocusedModuleName()));

		if (project == null) {
			return false;
		}

		// only permit installation if not applied so far
		if (fileManager.exists(pathResolver.getFocusedIdentifier(Path.SPRING_CONFIG_ROOT, APPLICATION_CONTEXT_CONTENTRESOLVER_XML))) {

			return false;
		}

		return projectOperations.isFocusedProjectAvailable()
				&& fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "/WEB-INF/web.xml"));
	}

	public void installHttpContentNegotiation() {

		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(getClass());

		// Add dependencies to POM
		updateDependencies(configuration, this.projectOperations.getFocusedModuleName());

		// copy the template across
		copyFileTemplate(Path.SPRING_CONFIG_ROOT, APPLICATION_CONTEXT_CONTENTRESOLVER_XML,
				"applicationContext-contentresolver-template.xml");

		//
		String rootPackageName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();
		copyFileTemplate(Path.SRC_MAIN_JAVA, (rootPackageName + ".util").replace(".", "/") + "/JsonObjectMapperFactory.java",
				"JsonObjectMapperFactory-template.java");
		copyFileTemplate(Path.SRC_MAIN_JAVA, (rootPackageName + ".util").replace(".", "/") + "/MappingJacksonJsonpView.java",
				"MappingJacksonJsonpView-template.java");

	}

	private void copyFileTemplate(Path path, String targetName, String templateName) {

		String targetFileDestination = pathResolver.getFocusedIdentifier(path, targetName);

		OutputStream outputStream = null;
		if (!fileManager.exists(targetFileDestination)) {
			try {

				String input = IOUtils.toString(FileUtils.getInputStream(getClass(), templateName));

				input = input
						.replace("__TOP_LEVEL_PACKAGE__", projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName());

				outputStream = fileManager.createFile(targetFileDestination).getOutputStream();
				IOUtils.write(input.getBytes(), outputStream);
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			} finally {
				IOUtils.closeQuietly(outputStream);
			}
		}
	}

	public boolean isInstallCORS() {

		if (!projectOperations.isFocusedProjectAvailable()
				|| !fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "/WEB-INF/web.xml"))) {
			return false;
		}

		String rootPackageName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();

		String corsFilePath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_JAVA, (rootPackageName + ".util").replace(".", "/")
				+ "/CorsFilter.java");

		return !fileManager.exists(corsFilePath);
	}

	public void installCORS() {
		String rootPackageName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();
		copyFileTemplate(Path.SRC_MAIN_JAVA, (rootPackageName + ".util").replace(".", "/") + "/CorsFilter.java", "CorsFilter-template.java");

		// update web.xml
		String webXml = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml");
		Validate.isTrue(fileManager.exists(webXml), "web.xml not found; cannot continue");

		try {
			MutableFile mutableWebXml = fileManager.updateFile(webXml);
			Document webXmlDoc = XmlUtils.getDocumentBuilder().parse(mutableWebXml.getInputStream());
			String filterClassName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName() + ".util.CorsFilter";
			WebXmlUtils.addFilter("cors", filterClassName, "/*", webXmlDoc, null);

			XmlUtils.writeXml(mutableWebXml.getOutputStream(), webXmlDoc);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean isInstallContentOxmBinding() {

		ProjectMetadata project = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier(this.projectOperations
				.getFocusedModuleName()));

		if (project == null) {
			return false;
		}

		// only permit if the HttpResourceRepresentation is already setup
		return fileManager.exists(pathResolver.getFocusedIdentifier(Path.SPRING_CONFIG_ROOT, APPLICATION_CONTEXT_CONTENTRESOLVER_XML));
	}

	public void installContentOxmBinding(JavaType typeName) {

		Validate.notNull(typeName, "Java type required");

		String id = typeLocationService.getPhysicalTypeIdentifier(typeName);
		if (id == null) {
			logger.warning("Cannot locate source for '" + typeName.getFullyQualifiedTypeName() + "'");
			return;
		}

		String contentresolver = pathResolver.getFocusedIdentifier(Path.SPRING_CONFIG_ROOT, APPLICATION_CONTEXT_CONTENTRESOLVER_XML);

		MutableFile mutableFile = fileManager.updateFile(contentresolver);

		Document document;
		try {
			document = XmlUtils.getDocumentBuilder().parse(mutableFile.getInputStream());
		} catch (Exception ex) {
			throw new IllegalStateException("Could not open '" + contentresolver + "'", ex);
		}

		Element rootElement = (Element) document.getFirstChild();

		// Element oxmJaxbMarshaller =
		// XmlUtils.findFirstElement("//*[namespace-uri()='http://www.springframework.org/schema/oxm' and @id='jaxbMarshaller']",
		// rootElement);

		Element oxmJaxbMarshaller = XmlUtils.findFirstElement("/beans/jaxb2-marshaller", rootElement);
		if (oxmJaxbMarshaller == null) {
			// first time -> create the jaxb2 marshaller elements
			oxmJaxbMarshaller = document.createElement("oxm:jaxb2-marshaller");
			oxmJaxbMarshaller.setAttribute("id", "jaxbMarshaller");
			XmlUtils.findFirstElement("/beans", rootElement).appendChild(oxmJaxbMarshaller);

			// "//servlet[servlet-class='net.tzolov.http.ProxyServlet']/servlet-name",
			Element defaultViews = XmlUtils
					.findFirstElement(
							"/beans/bean[@class='org.springframework.web.servlet.view.ContentNegotiatingViewResolver']/property[@name = 'defaultViews']/list",
							rootElement);
			Element marshallingView = document.createElement("bean");
			marshallingView.setAttribute("class", "org.springframework.web.servlet.view.xml.MarshallingView");
			Element marshallingViewProperty = document.createElement("property");
			marshallingViewProperty.setAttribute("name", "marshaller");
			marshallingViewProperty.setAttribute("ref", "jaxbMarshaller");
			marshallingView.appendChild(marshallingViewProperty);

			defaultViews.appendChild(marshallingView);
		}

		Element oxmBinding = document.createElement("oxm:class-to-be-bound");
		oxmBinding.setAttribute("name", typeName.getFullyQualifiedTypeName());

		oxmJaxbMarshaller.appendChild(oxmBinding);

		XmlUtils.writeXml(mutableFile.getOutputStream(), document);
	}

	private void updateDependencies(Element configuration, final String moduleName) {
		List<Element> databaseDependencies = XmlUtils.findElements("/configuration/httprepresentations/dependencies/dependency",
				configuration);

		for (Element dependencyElement : databaseDependencies) {
			projectOperations.addDependency(moduleName, new Dependency(dependencyElement));
		}
	}

}
