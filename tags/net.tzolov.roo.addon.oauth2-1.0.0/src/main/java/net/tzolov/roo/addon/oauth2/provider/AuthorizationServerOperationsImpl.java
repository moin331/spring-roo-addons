package net.tzolov.roo.addon.oauth2.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.tzolov.roo.addon.oauth2.OAuth2Common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.addon.propfiles.PropFileOperations;
import org.springframework.roo.addon.web.mvc.controller.WebMvcOperations;
import org.springframework.roo.addon.web.mvc.jsp.tiles.TilesOperations;
import org.springframework.roo.classpath.operations.AbstractOperations;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.LogicalPath;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Property;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.DomUtils;
import org.springframework.roo.support.util.FileUtils;
import org.springframework.roo.support.util.WebXmlUtils;
import org.springframework.roo.support.util.XmlElementBuilder;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides OAuth2 security provider installation services.
 *
 * @author Christian Tzolov (christian@tzolov.net)
 */
@Component
@Service
public class AuthorizationServerOperationsImpl extends AbstractOperations implements AuthorizationServerOperations {

	// Fields
	@Reference private PathResolver pathResolver;
	@Reference private ProjectOperations projectOperations;
	@Reference private TilesOperations tilesOperations;
	@Reference private PropFileOperations propFileOperations;
	
	public boolean isAuthorizationServerInstallationPossible() {
		return projectOperations.isFocusedProjectAvailable() 
				&& fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml")) 
				&& !hasDependencyExcludingVersion(OAuth2Common.SPRING_SECURITY) 
				&& !hasDependencyExcludingVersion(OAuth2Common.SPRING_OAUT_SECURITY);
	}

	private boolean hasDependencyExcludingVersion(Dependency dependencyDef ) {
		Set<Dependency> dependecies = projectOperations.getFocusedModule().getDependenciesExcludingVersion(dependencyDef);
		
		if (dependecies != null && dependecies.size() > 0) {
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.tzolov.roo.addon.oauth2.provider.Oauth2AuthorizeServerOperations#installOAuth2Security()
	 */
	public void installAuthorizationServer() {
		//Update the Favicon and the Banner
		updateIconAndLogo();
		
		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(getClass());

		// Add POM properties
		updatePomProperties(configuration, projectOperations.getFocusedModuleName());

		// Add POM oauth2 repository
		updateRepository(configuration,  projectOperations.getFocusedModuleName());
		
		// Add dependencies to POM
		updateDependencies(configuration, projectOperations.getFocusedModuleName());

		// Copy the template across
		copyFileTemplate(Path.SPRING_CONFIG_ROOT, "applicationContext-oauth2-provider.xml", "applicationContext-oauth2-authorize-server-template.xml");
		
		String rootPackageName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();		
//		copyFileTemplate(Path.SRC_MAIN_JAVA, rootPackageName.replace(".", "/")  + "/ClientDetailsUserDetailsService.java", "ClientDetailsUserDetailsService-template.java");
		copyFileTemplate(Path.SRC_MAIN_JAVA, (rootPackageName + ".web").replace(".", "/")  + "/OAuthAccessConfirmationController.java", "OAuthAccessConfirmationController-template.java");
		
		copyFileTemplate(Path.SRC_MAIN_WEBAPP, "WEB-INF/views/login.jspx", "login.jspx");		
		copyFileTemplate(Path.SRC_MAIN_WEBAPP, "WEB-INF/views/access_confirmation.jspx", "access_confirmation.jspx");

		if (fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/views/views.xml"))) {
			tilesOperations.addViewDefinition("", pathResolver.getFocusedPath(Path.SRC_MAIN_WEBAPP), "login", TilesOperations.PUBLIC_TEMPLATE, "/WEB-INF/views/login.jspx");
			tilesOperations.addViewDefinition("", pathResolver.getFocusedPath(Path.SRC_MAIN_WEBAPP), "access_confirmation", TilesOperations.PUBLIC_TEMPLATE, "/WEB-INF/views/access_confirmation.jspx");
		}

		String webXmlPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml");
		Document webXmlDocument = XmlUtils.readXml(fileManager.getInputStream(webXmlPath));
		WebXmlUtils.addFilterAtPosition(WebXmlUtils.FilterPosition.BETWEEN, WebMvcOperations.HTTP_METHOD_FILTER_NAME, WebMvcOperations.OPEN_ENTITYMANAGER_IN_VIEW_FILTER_NAME, AuthorizationAndResourceServerOperations.SECURITY_FILTER_NAME, "org.springframework.web.filter.DelegatingFilterProxy", "/*", webXmlDocument, null);
		fileManager.createOrUpdateTextFileIfRequired(webXmlPath, XmlUtils.nodeToString(webXmlDocument), false);

		// Include static view controller handler to webmvc-config.xml
		String webConfigPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/spring/webmvc-config.xml");
		Document webConfigDocument = XmlUtils.readXml(fileManager.getInputStream(webConfigPath));
		Element webConfig = webConfigDocument.getDocumentElement();
		Element viewController = DomUtils.findFirstElementByName("mvc:view-controller", webConfig);
		Validate.notNull(viewController, "Could not find mvc:view-controller in " + webConfig);
		viewController.getParentNode().insertBefore(new XmlElementBuilder("mvc:view-controller", webConfigDocument).addAttribute("path", "/login").build(), viewController);

		
		//Replace the simpleMappingExceptionResolver by
		OAuth2Common.replaceSimpleMappingExceptionResolver(webConfigDocument);
		
		// Add the "oauth2" namespace to the Spring config file
		OAuth2Common.addNamespaces(webConfig);
		
		// Add the authorization and the token endpoints
		Element oauthAuthorizationServerElement  = new XmlElementBuilder("oauth:authorization-server", webConfigDocument)
			.addAttribute("client-details-service-ref", "clientDetails")
			.addAttribute("token-services-ref", "tokenServices").build();
		Element oauthAuthorizationCode  = new XmlElementBuilder("oauth:authorization-code", webConfigDocument).build();
		Element oauthImplicit  = new XmlElementBuilder("oauth:implicit", webConfigDocument).build();
		Element oauthClientCredentials  = new XmlElementBuilder("oauth:client-credentials", webConfigDocument).build();
		Element oauthPassword  = new XmlElementBuilder("oauth:password", webConfigDocument).build();
		oauthAuthorizationServerElement.appendChild(oauthAuthorizationCode);
		oauthAuthorizationServerElement.appendChild(oauthImplicit);
		oauthAuthorizationServerElement.appendChild(oauthClientCredentials);
		oauthAuthorizationServerElement.appendChild(oauthPassword);		
		viewController.getParentNode().appendChild(oauthAuthorizationServerElement);
			
		
		fileManager.createOrUpdateTextFileIfRequired(webConfigPath, XmlUtils.nodeToString(webConfigDocument), false);
        
		//Update the security related massages
		propFileOperations.changeProperty(projectOperations
                .getPathResolver().getFocusedPath(Path.SRC_MAIN_WEBAPP), "WEB-INF/i18n/messages.properties", 
                "security_login_title", "OAuth 2.0 User Login", true);
		propFileOperations.changeProperty(projectOperations
                .getPathResolver().getFocusedPath(Path.SRC_MAIN_WEBAPP), "WEB-INF/i18n/messages.properties", 
                "security_login_message", "You have tried to access a Protected Resource. By default you can login as \"resourceOwner\", with a password of \"resourceOwner\".", true);		

	}
	
	private void updateIconAndLogo() {
		this.fileManager.delete(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "images/banner-graphic.png"));
		this.fileManager.delete(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "images/favicon.ico"));
		final LogicalPath webappPath = Path.SRC_MAIN_WEBAPP.getModulePathId(projectOperations.getFocusedModuleName());
		copyDirectoryContents("images/*.*", pathResolver.getIdentifier(webappPath, "images"), false);
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
	

	private void copyFileTemplate(Path path, String targetName, String templateName) {
				
		String targetFileDestination = pathResolver.getFocusedIdentifier(path, targetName);

		OutputStream outputStream = null;
		if (!fileManager.exists(targetFileDestination)) {
			try {								
				String input = IOUtils.toString(FileUtils.getInputStream(getClass(), templateName));
				input = input.replace("__TOP_LEVEL_PACKAGE__", projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName());

				outputStream = fileManager.createFile(targetFileDestination).getOutputStream();
				IOUtils.write(input, outputStream);
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			} finally {
				IOUtils.closeQuietly(outputStream);
			}
		}
	}
}