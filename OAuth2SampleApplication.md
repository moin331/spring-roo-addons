# Sample OAuth2 Applications #

The Web Client is a quiz generator application that renders a dictionary (e.g map of questions and answers) into user friendly web interface. The dictionary is provided (in JSON format) by a protected web service owned by the resource owner.

The **OAuth2** (and particularly **Authorization Code Grant** type) is used to grant the Web Client an access to the dictionary service.

Activity diagram below illustrates the authorization flow

![http://spring-roo-addons.googlecode.com/files/AuthorizeCodeActivityDiagramV2.png](http://spring-roo-addons.googlecode.com/files/AuthorizeCodeActivityDiagramV2.png)

Additional information about OAuth2:
  * [SpringSecurity OAuth2 Developers Guide](https://github.com/SpringSource/spring-security-oauth/wiki/oauth2)
  * [OAuth 2.0 Authorization Framework - Authorization Code Grant](http://tools.ietf.org/html/draft-ietf-oauth-v2-26#page-23)

## 1. Prepare OAuut2  MySQL schema ##
Follow the instructions at: [OAuth2SampleApplicationDBSetup](OAuth2SampleApplicationDBSetup.md)

## 2. Create OAuth2 Authorization Server Roo Project (as) ##
_(**Note:** Required SpringRoo 1.2.2 or higher and Java 6 or higher )_
```
project --topLevelPackage net.tzolov --projectName as --java 6
persistence setup --database MYSQL --provider HIBERNATE 
web mvc setup 
osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.roo.addon.oauth2-1.1.0.jar 
oauth2 authorization-server setup
// update the database.properties (inside src\main\resources\META-INF\spring\) to point to the oauth2 database created in step 1
```

## 3. Create Protected Resource Roo Project (res) ##
_(**Note:** Required SpringRoo 1.2.2 or higher and Java 6 or higher )_
```
project --topLevelPackage net.tzolov --projectName res --java 6
persistence setup --database MYSQL --provider HIBERNATE 
web mvc setup 
security setup
osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.roo.addon.oauth2-1.1.0.jar 
oauth2 resource setup --protectedResourceName MyResource 
// update the database.properties to point to the oauth2 database created in step 1
```

## 4. Create OAuth2 Web Client Application Roo Project (webapp) ##
_(**Note:** Required SpringRoo 1.2.2 or higher and Java 6 or higher )_
```
project --topLevelPackage net.tzolov --projectName webapp --java 6
persistence setup --database HYPERSONIC_IN_MEMORY --provider HIBERNATE 
web mvc setup 
security setup
osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.roo.addon.oauth2-1.1.0.jar 
oauth2 web-client setup --authorizationServerUri http://localhost:8080/as --protectedResourceUri http://localhost:8181/res/myresource
```

## 5. Build (mvn clean install) and runn all wars in a tomcat (or jetty) container ##

_(Note: because all application are running on the same host they have to start on different ports)_

**Start the tomcat container from the authorization-server top directory:
```
 mvn tomcat:run -Dmaven.tomcat.port=8080 
```**

**Start the tomcat container from the webapp's top directory (use port 8686):
```
 mvn tomcat:run -Dmaven.tomcat.port=8686 
```**

**Start the tomcat container from the resource project's top directory (use port 8181):
```
 mvn tomcat:run -Dmaven.tomcat.port=8181 
```**

