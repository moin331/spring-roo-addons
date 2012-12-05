package __TOP_LEVEL_PACKAGE__.util;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.introspect.BasicClassIntrospector;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;



public class JsonObjectMapperFactory {


	public static ObjectMapper createObjectMappe() {
		
		JaxbAnnotationIntrospector jaxbAnnotationIntrospector = new JaxbAnnotationIntrospector();
		
		AnnotationIntrospector jsonAnnotationIntrospector = new JacksonAnnotationIntrospector();

		AnnotationIntrospector introspectoPair = new AnnotationIntrospector.Pair(
				jaxbAnnotationIntrospector, jsonAnnotationIntrospector);

		DeserializationConfig deserializationConfig = new DeserializationConfig(
				BasicClassIntrospector.instance, introspectoPair,
				VisibilityChecker.Std.defaultInstance(), null, null,
				TypeFactory.defaultInstance(), null);

		SerializationConfig serializationConfig = new SerializationConfig(
				BasicClassIntrospector.instance, introspectoPair,
				VisibilityChecker.Std.defaultInstance(), null, null,
				TypeFactory.defaultInstance(), null);

		//serializationConfig.withSerializationInclusion(Inclusion.NON_NULL);

		ObjectMapper jsonObjectMapper = new ObjectMapper();

		jsonObjectMapper.setSerializationConfig(serializationConfig);

		jsonObjectMapper.setDeserializationConfig(deserializationConfig);

		return jsonObjectMapper;
	}

}
