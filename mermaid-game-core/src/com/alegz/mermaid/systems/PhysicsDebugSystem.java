package com.alegz.mermaid.systems;

import com.alegz.mermaid.rendering.PlatformerCamera;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsDebugSystem extends EntitySystem
{
	private World world;
	private Box2DDebugRenderer debugRenderer;
    
    private PlatformerCamera camera;
 
    public PhysicsDebugSystem(World world, PlatformerCamera camera)
    {
        this.world = world;
        debugRenderer = new Box2DDebugRenderer();
        
        this.camera = camera;
    }
 
    public void update(float deltaTime)
    {
        debugRenderer.render(world, camera.getProjMatrix());
    }
}
