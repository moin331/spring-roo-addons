### Indicate that the (Christian Tzolov) signature is trusted ###
This is necessary if you are to install addons singed with this signature
```
roo> pgp trust --keyId 0xACB27429
```

### List all available addons registered in RooBot ###
```
roo> addon list
```

Amongst all addons you will see the **jaxb**, **httprepresentations**, **executable-war** and the **servletproxy** addons.

### Install an addon from the RooBot repository ###

```
roo> addon install --bundleSymbolicName net.tzolov.jaxb.roo.addon
roo> addon install --bundleSymbolicName net.tzolov.httprepresentations.roo.addon
roo> addon install --bundleSymbolicName net.tzolov.jetty.executable.war.roo.addon
roo> addon install --bundleSymbolicName net.tzolov.http.servletproxy.roo.addon
```

Then if you perform 'osgi ps' you should seed the following entries in the outpu:
```
...
[  75] [Active     ] [    1] net.tzolov.jaxb.roo.addon (1.1.6)
[  76] [Active     ] [    1] net.tzolov.httprepresentations.roo.addon (1.1.8)
[  77] [Active     ] [    1] net.tzolov.jetty.executable.war.roo.addon (1.0.0)
[  78] [Active     ] [    1] net.tzolov.http.servletproxy.roo.addon (0.1.4)
...
```