package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.components.ButtonComponent;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class ButtonSystem extends EntitySystem
{
	private PlatformerCamera camera;
	private Entity activeButton;
	
	private List<Entity> buttonEntities;
	
	private ObjectMap<Entity, UITransformComponent> uiTransformComponents;
	private ObjectMap<Entity, ButtonComponent> buttonComponents;
	
	public ButtonSystem(PlatformerCamera camera) 
	{
		this.camera = camera;
		activeButton = null;
		
		buttonEntities = new ArrayList<>();
	}
	
	public void start(Engine engine) 
	{
		uiTransformComponents = engine.getComponentStorage(UITransformComponent.class);
		buttonComponents = engine.getComponentStorage(ButtonComponent.class);
	}
	
	private Vector2 getButtonPosition(UITransformComponent transform, Vector2 pivot)
	{
		Vector2 position = transform.position.cpy();
		position.x += transform.anchor.x * camera.getUIWidth();
		position.y += transform.anchor.y * camera.getUIHeight();
		position.x -= transform.scale.x * (pivot.x);
		position.y -= transform.scale.y * (pivot.y);
		return position;
	}
	
	public void update(float deltaTime)
	{
		Vector2 mousePos = camera.getScreenToUIPosition(Gdx.input.getX(), Gdx.input.getY());
		boolean mouseClick = Gdx.input.isButtonPressed(Buttons.LEFT);
		
		if (activeButton == null)
		{
			for (Entity entity : buttonEntities)
			{
				UITransformComponent transform = uiTransformComponents.get(entity);
				ButtonComponent button = buttonComponents.get(entity);
				
				Vector2 position = getButtonPosition(transform, button.pivot);
				boolean contains = mousePos.x > position.x && mousePos.x < position.x + transform.scale.x &&
								   mousePos.y > position.y && mousePos.y < position.y + transform.scale.y;
				
				if (contains && mouseClick)
				{
					button.state = 2;
					activeButton = entity;
				}
								   
				switch(button.state)
				{
				case 0:
					if (contains)
						button.state = 1;
					break;
				case 1:
					if (!contains)
						button.state = 0;
					break;
				}
			}
		}
		else
		{
			UITransformComponent transform = uiTransformComponents.get(activeButton);
			ButtonComponent button = buttonComponents.get(activeButton);
			
			Vector2 position = getButtonPosition(transform, button.pivot);
			boolean contains = mousePos.x > position.x && mousePos.x < position.x + transform.scale.x &&
							   mousePos.y > position.y && mousePos.y < position.y + transform.scale.y;
			
			if (!contains)
			{
				button.state = 0;
				activeButton = null;
			}
			else if (!mouseClick)
			{
				button.state = 1;
				if (button.action != null)
					button.action.onClick();
				activeButton = null;
			}
		}
		
		for (Entity entity : buttonEntities)
		{
			ButtonComponent button = buttonComponents.get(entity);
			
			switch(button.state)
			{
			case 0:
				if (button.defaultSprite != null)
					button.sprite = button.defaultSprite;
				break;
			case 1:
				if (button.highlightSprite != null)
					button.sprite = button.highlightSprite;
				break;
			case 2:
				if (button.pressedSprite != null)
					button.sprite = button.pressedSprite;
				break;
			}
		}
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, UITransformComponent.class) &&
			engine.hasComponent(entity, ButtonComponent.class))
			buttonEntities.add(entity);
	}
	
	public void entityRemoved(Engine engine, Entity entity)
	{
		if (buttonEntities.contains(entity))
			buttonEntities.remove(entity);
	}
}
