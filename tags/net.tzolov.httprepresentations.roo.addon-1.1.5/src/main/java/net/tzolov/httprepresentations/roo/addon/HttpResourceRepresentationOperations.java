package net.tzolov.httprepresentations.roo.addon;

import org.springframework.roo.model.JavaType;

public interface HttpResourceRepresentationOperations {

	public abstract boolean isInstallHttpResourceRepresentationAvailable();

	public abstract void installHttpResourceRepresentation();

	public abstract boolean isInstallContentOxmBinding();

	public abstract void installContentOxmBinding(JavaType typeName);

}