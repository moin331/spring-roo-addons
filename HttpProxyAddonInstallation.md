### How to install http-proxy Spring Roo Addon (latest version: 0.1.4) ###

  * Uninstall the older addon versions
```
  roo> osgi uninstall --bundleSymbolicName net.tzolov.http.servletproxy.roo.addon
```
  * Install the addon from the central OBR repostiory (RooBot)
```
  roo> addon install --bundleSymbolicName net.tzolov.http.servletproxy.roo.addon
```
> > The first time you would need to approve the signature: **roo> pgp trust --keyId 0xACB27429**


> Alternatively you can install it directly from the googlecode site:
```
  roo> osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.http.servletproxy.roo.addon-0.1.4.jar 
```
  * Verify the installation:
```
  roo> osgi ps 
```
  * You should see something like:
```
[  90] [Active     ] [    1] Spring Roo -  Http-Proxy To Allow Cross-Domain Access (0.1.4)
```
  * From the Roo Shell type **help** to see the list of all supported commands. The following line should appear amongst others entries:
```
roo> help http-proxy
* http-proxy add - Add new ServletProxy configuration
* http-proxy cleanAll - Remove all existing ServletProxy configurations
* http-proxy list - Shows the existing ServletProxy configurations
* http-proxy remove - Remove an existing ServletProxy configuration
```