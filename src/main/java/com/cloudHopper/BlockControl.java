package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BlockControl extends Component {

    private Point2D xDirection, yDirection;

    public void message() {
        if (!getb("blockDialogue")){
            getNotificationService().pushNotification("Press S to pull blocks.");
            set("blockDialogue", true);
        }
    }

    public void moveBlock(Entity player, Double pushingSpeed) {
        entity.getComponent(PhysicsComponent.class).setVelocityX(pushingSpeed);
    }
}
