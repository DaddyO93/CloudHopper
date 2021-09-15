package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BlockControl extends Component {

    public void message() {
        if (!getb("blockDialogue")){
            getNotificationService().pushNotification("Press S to pull blocks.");
            set("blockDialogue", true);
        }
    }

    public void moveBlock(Double pushingSpeed) {
        entity.getComponent(PhysicsComponent.class).setVelocityX(pushingSpeed);
    }
}
