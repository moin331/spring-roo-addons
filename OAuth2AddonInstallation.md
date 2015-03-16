_**NOTE:** **Not** backward compatible! Supports only Roo 1.2.2 or newer_
### How to install the OAuth2 Spring Roo Addon (latest version: 1.1.0) ###

  * Uninstall the older addon versions
```
  roo> osgi uninstall --bundleSymbolicName net.tzolov.roo.addon.oauth2
```
  * Install the addon from the central OBR repostiory (RooBot)
```
  roo> addon install bundle --bundleSymbolicName net.tzolov.roo.addon.oauth2
```
> > The first time you would need to approve the signature: **roo> pgp trust --keyId 0xACB27429**


> Alternatively you can install it directly from the googlecode site:
```
  roo> osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.roo.addon.oauth2-1.1.0.jar 
```

  * Verify the installation:
```
  roo> osgi ps 
```
  * You should see something like:
```
...
[  72] [Active     ] [    1] Spring Roo - OAuth2 Provider/Client Setup (1.1.0)
```
  * From the Roo Shell type **help** to see the list of all supported commands. The following line should appear amongst others entries:
```
...
* oauth2 authorization-server setup - Install Spring Security OAuth2 Authorization Server. Requires 'web mvc setup' addon to be installed first
* oauth2 collocated-authorization-and-resource-server setup - Install Spring Security OAuth2 Provider together with a Protected Resource (shared in-memory TokenStore)
* oauth2 resource setup - Add new OAuth2 protected resource to your project. Requires the 'web mvc steup' and 'security setup' addons to be installed first
* oauth2 web-client setup - Install Spring Security OAuth2 Client into your project
```