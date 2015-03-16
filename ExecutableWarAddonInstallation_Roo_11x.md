### How to install executable-war-addon Spring Roo Addon (latest version: 1.0.0) ###

  * Uninstall the older addon versions
```
  roo> osgi uninstall --bundleSymbolicName net.tzolov.jetty.executable.war.roo.addon
```
  * Install the addon from the central OBR repostiory (RooBot)
```
  roo> addon install --bundleSymbolicName net.tzolov.jetty.executable.war.roo.addon
```
> > The first time you would need to approve the signature: **roo> pgp trust --keyId 0xACB27429**


> Alternatively you can install it directly from the googlecode site:
```
  roo> osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.jetty.executable.war.roo.addon-1.0.0.jar 
```
  * Verify the installation:
```
  roo> osgi ps 
```
  * You should see something like:
```
...
[  60] [Active     ] [    1] Spring Roo - Executable War (1.0.0)
```
  * From the Roo Shell type **help** to see the list of all supported commands. The following line should appear amongst others entries:
```
...
executable war setup - Creates an <Your Project Name>-EXECWAR.war executable application. Usage: java -jar <Your Project Name>-EXECWAR.war
```