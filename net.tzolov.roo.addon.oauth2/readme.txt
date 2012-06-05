
The instructions show how to use the OAuth2 Addon in real projects

//1. Prepare OAuut2  MySQL schema.
// Create MySQL oauth2 database  with the following tables:
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication_id` varchar(256) DEFAULT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  `authentication` blob,
  `refresh_token` varchar(256) DEFAULT NULL,
  `access_token_validity` INTEGER,
  `refresh_token_validity` INTEGER  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oauth_client_details` (
  `client_id` varchar(256) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oauth_code` (
  `code` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

//Required Roo 1.2.2

//2. Create OAuth2 Authorization Server Roo Project (as)
project --topLevelPackage net.tzolov --projectName as --java 6
persistence setup --database MYSQL --provider HIBERNATE 
web mvc setup 
addon list --refresh 
addon install bundle --bundleSymbolicName net.tzolov.roo.addon.oauth2
oauth2 authorization-server setup
// update the database.properties to point to the oauth2 database created in step 1


//3. Create Protected Resource Roo Project (res)
project --topLevelPackage net.tzolov --projectName res --java 6
persistence setup --database MYSQL --provider HIBERNATE 
web mvc setup 
security setup
addon install bundle --bundleSymbolicName net.tzolov.roo.addon.oauth2
addon list --refresh 
addon install bundle --bundleSymbolicName net.tzolov.roo.addon.oauth2
oauth2 resource setup --protectedResourceName MyResource 
// update the database.properties to point to the oauth2 database created in step 1

//4. Create OAuth2 Web Client Application Roo Project (webapp)
project --topLevelPackage net.tzolov --projectName webapp --java 6
persistence setup --database HYPERSONIC_IN_MEMORY --provider HIBERNATE 
web mvc setup 
security setup
addon list --refresh 
addon install bundle --bundleSymbolicName net.tzolov.roo.addon.oauth2
oauth2 web-client setup --authorizationServerUri http://localhost:8080/as --protectedResourceUri http://localhost:8080/res/myresource

//5. Build (mvn clean install) and runn all wars in a tomcat (or jetty) container



