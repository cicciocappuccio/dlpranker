package com.neuralnoise.cache;

import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.ObjectProperty;

public abstract class AbstractRoleCache {

	public AbstractRoleCache() { }
	
	public abstract Boolean get(ObjectProperty property, Individual subject, Individual object);
	
	public abstract void addElement(ObjectProperty property, Individual subject, Individual object, Boolean entailed);

	
}
