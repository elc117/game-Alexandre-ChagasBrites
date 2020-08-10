package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;

public class TrashComponent implements Component
{
	public Class<? extends Component> getComponentClass()
	{
		return TrashComponent.class;
	}
}
