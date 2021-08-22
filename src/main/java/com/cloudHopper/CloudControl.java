package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import java.util.Random;

public class CloudControl extends Component {

    @Override
    public void onUpdate(double tpf) {
        moveCloud();
    }

    private void moveCloud() {
        PhysicsComponent physics = new PhysicsComponent();

        physics.setVelocityX(-1 * new Random().nextInt(100)+1);
        System.out.println("moving cloud");

    }
}
