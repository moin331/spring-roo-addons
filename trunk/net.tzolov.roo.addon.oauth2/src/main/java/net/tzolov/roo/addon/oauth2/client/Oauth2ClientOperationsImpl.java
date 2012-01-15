package net.tzolov.roo.addon.oauth2.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.tzolov.roo.addon.oauth2.OAuth2Common;
import net.tzolov.roo.addon.oauth2.provider.AuthorizationAndResourceServerOperationsImpl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.addon.web.mvc.jsp.tiles.TilesOperations;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Property;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.CollectionUtils;
import org.springframework.roo.support.util.DomUtils;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.FileUtils;
import org.springframework.roo.support.util.StringUtils;
import org.springframework.roo.support.util.XmlElementBuilder;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides OAuth2 Web Client installation services.
 *
 * Christian Tzolov (christian@tzolov.net)
 */
@Component
@Service
public class Oauth2ClientOperationsImpl implements Oauth2ClientOperations {

	// Fields
	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private ProjectOperations projectOperations;
	@Reference private TilesOperations tilesOperations;

	public boolean isOAuthClientInstallationPossible() {
		// Permit installation if they have a web project (as per ROO-342) and
		// no version of Spring Security is already installed.
		return projectOperations.isFocusedProjectAvailable() && fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml")) && hasDependencyExcludingVersion(OAuth2Common.SPRING_SECURITY) && !hasDependencyExcludingVersion(OAuth2Common.SPRING_OAUT_SECURITY);
	}

	private boolean hasDependencyExcludingVersion(Dependency dependencyDef ) {
		Set<Dependency> dependecies = projectOperations.getFocusedModule().getDependenciesExcludingVersion(dependencyDef);
		
		if (dependecies != null && dependecies.size() > 0) {
			return true;
		}
		
		return false;
	}
	
	public void installOAuthClient(String authorizationServerUri, String protectedResourceUri) {
		
		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(AuthorizationAndResourceServerOperationsImpl.class);

		// Add POM properties
		updatePomProperties(configuration, projectOperations.getFocusedModuleName());

		updateRepository(configuration,  projectOperations.getFocusedModuleName());
		
		// Add dependencies to POM
		updateDependencies(configuration, projectOperations.getFocusedModuleName());

		String securityAppContextPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_RESOURCES, "META-INF/spring/applicationContext-security.xml");
		Document securityAppContextDocument = XmlUtils.readXml(fileManager.getInputStream(securityAppContextPath));
		Element securityAppContext = securityAppContextDocument.getDocumentElement();
		Element httpChain = DomUtils.findFirstElementByName("http", securityAppContext);
		Assert.notNull(httpChain, "Could not find http in " + securityAppContext);
		
		boolean useExpressions = (httpChain.getAttribute("use-expressions") != null && httpChain.getAttribute("use-expressions").equalsIgnoreCase("true"));
		
		httpChain.insertBefore(new XmlElementBuilder("intercept-url", securityAppContextDocument).addAttribute("pattern", "/quiz/**").addAttribute("access", (useExpressions?"hasRole('ROLE_USER')":"ROLE_USER")).build(), httpChain.getFirstChild());

		httpChain.appendChild(new XmlElementBuilder("custom-filter", securityAppContextDocument).addAttribute("ref", "oauth2ClientFilter").addAttribute("after", "EXCEPTION_TRANSLATION_FILTER").build());

		securityAppContext.appendChild(new XmlElementBuilder("oauth:client", securityAppContextDocument).addAttribute("id", "oauth2ClientFilter").addAttribute("redirect-on-error", "${redirectOnError:false}").build());

		securityAppContext.appendChild(new XmlElementBuilder("oauth:resource", securityAppContextDocument)
			.addAttribute("id", "testResource").addAttribute("type", "authorization_code").addAttribute("client-id", "my-client-id")
			.addAttribute("client-secret", "secret").addAttribute("access-token-uri", "${accessTokenUri}")
			.addAttribute("user-authorization-uri", "${userAuthorizationUri}").addAttribute("scope", "read,write").build());

		securityAppContext.appendChild(new XmlElementBuilder("beans:bean", securityAppContextDocument)
			.addAttribute("class", "org.springframework.security.oauth2.client.OAuth2RestTemplate").addAttribute("c:resource-ref", "testResource").build());	
		
		if (StringUtils.isBlank(securityAppContext.getAttribute("xmlns:oauth"))) {
			securityAppContext.setAttribute("xmlns:oauth", "http://www.springframework.org/schema/security/oauth2");
			securityAppContext.setAttribute("xsi:schemaLocation", securityAppContext.getAttribute("xsi:schemaLocation") + "  " + "http://www.springframework.org/schema/security/oauth2" + " " + "http://www.springframework.org/schema/security/spring-security-oauth2.xsd");
		}
		if (StringUtils.isBlank(securityAppContext.getAttribute("xmlns:c"))) {
			securityAppContext.setAttribute("xmlns:c", "http://www.springframework.org/schema/c");
		}
		
		fileManager.createOrUpdateTextFileIfRequired(securityAppContextPath, XmlUtils.nodeToString(securityAppContextDocument), false);


		Map<String,String> subMap = new HashMap<String, String>();
		subMap.put("__TOP_LEVEL_PACKAGE__", projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName());
		subMap.put("__OAUTH_SERVER_URI__", authorizationServerUri);
		subMap.put("__PROTECTED_RESOURCE_URI__", protectedResourceUri);

		copyFileTemplate(Path.SRC_MAIN_WEBAPP, "WEB-INF/views/testResource.jspx", "testResource.jspx",subMap);
		if (fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/views/views.xml"))) {
			tilesOperations.addViewDefinition("", pathResolver.getFocusedPath(Path.SRC_MAIN_WEBAPP), "testResource", TilesOperations.PUBLIC_TEMPLATE, "/WEB-INF/views/testResource.jspx");
		}
		
		copyFileTemplate(Path.SRC_MAIN_RESOURCES, "META-INF/spring/oauth2.client.properties", "oauth2.client-template.properties", subMap);
		
		String rootPackageName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();				
		copyFileTemplate(Path.SRC_MAIN_JAVA, (rootPackageName + ".web").replace(".", "/")  + "/OAuthClientController.java", "OAuthClientController-template.java", subMap);
				
		// Include static view controller handler to webmvc-config.xml
		String webConfigPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/spring/webmvc-config.xml");
		Document webConfigDocument = XmlUtils.readXml(fileManager.getInputStream(webConfigPath));
		Element webConfig = webConfigDocument.getDocumentElement();

		// Add the "oauth2" namespace to the Spring config file
		if (StringUtils.isBlank(webConfig.getAttribute("xmlns:oauth"))) {
			webConfig.setAttribute("xmlns:oauth", "http://www.springframework.org/schema/security/oauth2");
			webConfig.setAttribute("xsi:schemaLocation", webConfig.getAttribute("xsi:schemaLocation") + "  " + "http://www.springframework.org/schema/security/oauth2" + " " + "http://www.springframework.org/schema/security/spring-security-oauth2.xsd");
		}
		
		//Replace the simpleMappingExceptionResolver by
		OAuth2Common.replaceSimpleMappingExceptionResolver(webConfigDocument);
				
		fileManager.createOrUpdateTextFileIfRequired(webConfigPath, XmlUtils.nodeToString(webConfigDocument), false);

	}

	private void updatePomProperties(final Element configuration, final String moduleName) {
		List<Element> databaseProperties = XmlUtils.findElements("/configuration/spring-security/properties/*", configuration);
		for (Element property : databaseProperties) {
			projectOperations.addProperty(moduleName, new Property(property));
		}
	}

	private void updateDependencies(final Element configuration, final String moduleName) {
		List<Dependency> dependencies = new ArrayList<Dependency>();
		List<Element> securityDependencies = XmlUtils.findElements("/configuration/spring-security/dependencies/dependency", configuration);
		for (Element dependencyElement : securityDependencies) {
			
			dependencies.add(new Dependency(dependencyElement));
		}
		projectOperations.addDependencies(moduleName, dependencies);		
	}
	
	private void updateRepository(final Element configuration, final String moduleName) {
		List<Repository> repositories = new ArrayList<Repository>();
		List<Element> securityRepositories = XmlUtils.findElements("/configuration/spring-security/repositories/repository", configuration);
		for (Element repositoryElement : securityRepositories) {
			repositories.add(new Repository(repositoryElement));
		}
		projectOperations.addRepositories(moduleName, repositories);		
	}
	
	

	private void copyFileTemplate(Path path, String targetName,
			String templateName, Map<String, String> substitusionMap) {

		String targetFileDestination = pathResolver.getFocusedIdentifier(path, targetName);

		if (!fileManager.exists(targetFileDestination)) {
			try {

				String input = FileCopyUtils.copyToString(new InputStreamReader(FileUtils.getInputStream(getClass(), templateName)));

				if (!CollectionUtils.isEmpty(substitusionMap)) {
					for (Entry<String, String> substitutionEntry : substitusionMap .entrySet()) {
						input = input.replace(substitutionEntry.getKey(), substitutionEntry.getValue());
					}
				}

				FileCopyUtils.copy(input.getBytes(), fileManager.createFile(targetFileDestination) .getOutputStream());
				
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}
	}
}