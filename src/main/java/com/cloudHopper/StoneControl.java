package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

public class StoneControl extends Component {
    private Entity player = getGameWorld().getEntitiesByType(com.cloudHopper.EntityType.PLAYER).get(0);
    private PhysicsComponent physics;
    private double stoneMovementSpeed = geti("stoneMovementSpeed");

    @Override
    public void onAdded() {
        stoneMovementSpeed = stoneMovementSpeed * player.getScaleX();
    }

    @Override
    public void onUpdate(double tpf) {
        physics.setVelocityX(stoneMovementSpeed);
    }
}
