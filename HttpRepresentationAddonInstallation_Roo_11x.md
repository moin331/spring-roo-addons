### How to install the http-representations Spring Roo Addon (latest version: 1.1.8) ###

  * Uninstall the older addon versions
```
  roo> osgi uninstall --bundleSymbolicName net.tzolov.httprepresentations.roo.addon
```
  * Install the addon from the central OBR repostiory (RooBot)
```
  roo> addon install --bundleSymbolicName net.tzolov.httprepresentations.roo.addon
```
> > The first time you would need to approve the signature: **roo> pgp trust --keyId 0xACB27429**


> Alternatively you can install it directly from the googlecode site:
```
  roo> osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.httprepresentations.roo.addon-1.1.8.jar 
```

  * Verify the installation:
```
  roo> osgi ps 
```
  * You should see something like:
```
...
[  60] [Active     ] [    1] Spring Roo - HTTP Content Negotiating Resolver (1.1.8)
```
  * From the Roo Shell type **help** to see the list of all supported commands. The following line should appear amongst others entries:
```
...
http resource add oxm - Define a class for object to xml mapping response
http resource representation setup - Install HTTP Content Negotiation Resolver into your project
```