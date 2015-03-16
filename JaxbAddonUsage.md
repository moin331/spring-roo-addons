## How to use the jax-addon ##

  * Run from the Roo shell:
```
roo> jaxb xsd compiler setup
```
> Optional parameters:
    * **generateDirectory** -  Target directory for the generated code, target/generated-sources/xjc by default
    * **schemaDirectory**   -  Specifies the schema directory, src/main/resources by default
  * Drop your XSD files in the schema directory (src/main/resources by default) and re-build the project. All jaxb binding classes will be generated in the generate directory (by default target/generated-sources/xjc)

### Tutorials: ###
  * [Spring Roo Addons : JAXB2 binding and Content Negotiation Support Tutorial](http://tzolov.blogspot.com/2010/05/spring-roo-addons-jaxb2-binding-and.html) with [annotated roo.log](JaxbHttpContentNegotiationTutorialRooLog.md)