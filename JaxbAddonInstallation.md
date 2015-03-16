_**Compatibility:** Spring Roo 1.2.3.RELEASE, Spring 3.2.0.RELEASE, Java 1.6+_

_**Note:** This version is **not** backward compatible. For previous version check the
[Roo 1.1.x compatible installation instructions](JaxbAddonInstallation_Roo_11X.md)_

---

### How to install jaxb-addon Spring Roo Addon (latest version: 3.2.0) ###
  * Uninstall the older addon versions
```
  roo> osgi uninstall --bundleSymbolicName net.tzolov.jaxb.roo.addon
```
  * Install the addon from the central OBR repostiory (RooBot)
```
  roo> addon install --bundleSymbolicName net.tzolov.jaxb.roo.addon
```
> > The first time you would need to approve the signature: **roo> pgp trust --keyId 0xACB27429**


> Alternatively you can install it directly from the googlecode site:
```
  roo> osgi start --url http://spring-roo-addons.googlecode.com/files/net.tzolov.jaxb.roo.addon-3.2.0.jar 
```
  * Verify the installation:
```
  roo> osgi ps 
```
  * You should see something like:
```
...
[  60] [Active     ] [    1] Spring Roo - JAXB2 Schema complier (3.2.0)
```
  * From the Roo Shell type **help** to see the list of all supported commands. The following line should appear amongst others entries:
```
...
jaxb xsd compiler setup - Install JAXB2 maven plugin to generate Java classes from an XML schema with the xjc schema compiler tool.
```
Put the XML Schemas (XSDs) to be compiled into the schema directory (default is src/main/resources)