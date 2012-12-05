package net.tzolov.httprepresentations.roo.addon;

import org.springframework.roo.model.JavaType;


public interface HttpContentNegotiationOperations {

	boolean isInstallHttpContentNegotiationAvailable();

	void installHttpContentNegotiation();
	
	boolean isInstallContentOxmBinding();

	void installContentOxmBinding(JavaType typeName);	
	
	public boolean isInstallCORS();
	
	void installCORS();
}