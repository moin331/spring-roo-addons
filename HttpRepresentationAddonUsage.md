## How to use the http representation addon ##

For addon's installation instructions visit [HttpRepresentationAddonInstallation](HttpRepresentationAddonInstallation.md).

#### 1 HTTP Content Negotiation Setup ####
```
roo> http resource representation setup
```
Amongst other the setup will register JSON and JSONP (un)marshallar.

#### 2 JSON and JSONP representations ####
_Note: By default the json object mapper uses a plain object marshall/unmarshal strategy (e.g. maps all public fields)._

For JSON representation add **.json** postfix to the url. For example http://localhost:8080/myapp/myresource.json or http://localhost:8080/myapp/myresource/1.json

For JSONP representation add **.jsonp** postfix to the url and provide a **callback** url parmeter. For example http://localhost:8080/myapp/myresource.jsonp?callback=myhandler or http://localhost:8080/myapp/myresource/1.json?callback=myhandler

#### 3 XML representation using JAXB2 binding ####
You can generate JAXB2 classes either from an existing XML Schema (see the [Jaxb Addon](JaxbAddonInstallation.md) ) or JAXB2 annotate your existing domain/entity classes.

Then register one ore more of the JAXB2 annotated classes to the marshaller configuration.
```
roo> http resource add oxm --class my.package.MyJaxbClass
```
The my.package.MyJaxbClass will be added to the **oxm:jaxb2-marshaller** element of your applicationContext-contentresolver.xml file. When performed for the first time this command adds the JAXB2 (un)marshaller configuration as well.

Now you can access your data represented in XML by adding a **.xml** postfix to you urls. For example:
http://localhost:8080/myapp/myresource.xml or http://localhost:8080/myapp/myresource/1.xml

_Note: When an object is annotated with JAXB2 annotation the JSON/JSONP object mapper will use those annotation over the plain object mapping._

#### 4 CORS (Cross Origin Resource Sharing) ####
Enable [| client-side cross-origin requests](http://www.w3.org/TR/cors) in your web application
```
roo> http cross-origin-resource-sharing setup
```
This commands adds a new CorsFilter.java class and registers it to the web.xml. By default it permits all all HTTP methods (e.g. "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE" ) and all Origins (e.g "Access-Control-Allow-Origin", "**" ).
To change the default behaviour modify CorsFilter as needed.**

## Tutorials: ##
_Note: this tutorial is outdated!_
  * [Spring Roo Addons : JAXB2 binding and Content Negotiation Support Tutorial](http://tzolov.blogspot.com/2010/05/spring-roo-addons-jaxb2-binding-and.html) with [annotated roo.log](JaxbHttpContentNegotiationTutorialRooLog.md)