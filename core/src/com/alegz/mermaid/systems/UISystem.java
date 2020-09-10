package com.alegz.mermaid.systems;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.EntityList;
import com.alegz.ecs.EntityListListener;
import com.alegz.ecs.EntitySystem;
import com.alegz.mermaid.components.ButtonComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;

public class UISystem extends EntitySystem implements EntityListListener
{
	private PlatformerCamera camera;
	private Entity activeButton;
	
	private EntityList uiEntities;
	private EntityList buttonEntities;
	
	private ComponentMap<UITransformComponent> uiTransformComponents;
	private ComponentMap<ButtonComponent> buttonComponents;
	
	public UISystem(Engine engine, PlatformerCamera camera) 
	{
		super(engine);
		this.camera = camera;
		activeButton = null;
		
		uiEntities = engine.createEntityList().has(UITransformComponent.class);
		buttonEntities = engine.createEntityList().has(UITransformComponent.class, ButtonComponent.class);
		
		uiEntities.setListener(this);
		
		uiTransformComponents = engine.getComponentMap(UITransformComponent.class);
		buttonComponents = engine.getComponentMap(ButtonComponent.class);
	}
	
	private boolean buttonContains(UITransformComponent transform, ButtonComponent button, Vector2 mousePos)
	{
		Vector2 position = transform.uiPosition.cpy();
		position.x -= transform.scale.x * (button.pivot.x);
		position.y -= transform.scale.y * (button.pivot.y);
		
		return mousePos.x > position.x && mousePos.x < position.x + transform.scale.x &&
			   mousePos.y > position.y && mousePos.y < position.y + transform.scale.y;
	}
	
	@Override
	public void update(float deltaTime)
	{
		for (Entity entity : uiEntities)
		{
			UITransformComponent transform = uiTransformComponents.get(entity);
			if (transform.dirty)
				updateUIPosition(entity);
		}
		
		Vector2 mousePos = camera.getScreenToUIPosition(Gdx.input.getX(), Gdx.input.getY());
		boolean mouseClick = Gdx.input.isButtonPressed(Buttons.LEFT);
		
		if (activeButton == null)
		{
			for (Entity entity : buttonEntities)
			{
				UITransformComponent transform = uiTransformComponents.get(entity);
				ButtonComponent button = buttonComponents.get(entity);
				
				boolean contains = buttonContains(transform, button, mousePos);
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
			
			boolean contains = buttonContains(transform, button, mousePos);
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
	
	private void updateUIPosition(Entity entity)
	{
		UITransformComponent transform = uiTransformComponents.get(entity);
		transform.uiPosition.x = transform.position.x + transform.anchor.x * camera.getUIWidth();
		transform.uiPosition.y = transform.position.y + transform.anchor.y * camera.getUIHeight();
	}
	
	@Override
	public void resize(int width, int height)
	{
		for (Entity entity : uiEntities)
			updateUIPosition(entity);
	}

	@Override
	public void entityAdded(Engine engine, Entity entity) 
	{
		updateUIPosition(entity);
	}

	@Override
	public void entityRemoved(Engine engine, Entity entity) 
	{
		
	}
}
