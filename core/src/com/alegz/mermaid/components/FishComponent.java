package com.alegz.mermaid.components;

import com.alegz.mermaid.FishType;
import com.alegz.mermaid.ecs.Component;

public class FishComponent implements Component
{
	public FishType type;
	public float oldHeight = 0.0f;

	public Class<? extends Component> getComponentClass() 
	{
		return FishComponent.class;
	}
}
