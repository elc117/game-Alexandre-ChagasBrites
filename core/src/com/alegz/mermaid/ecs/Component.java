package com.alegz.mermaid.ecs;

public interface Component 
{
	//public Component cpy();
	
	public Class<? extends Component> getComponentClass();
}
