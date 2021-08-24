package com.cloudHopper;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CrateControl extends Component {
    private void messages(double x, double y) {
        getNotificationService().pushNotification("You freed a cloud! Move quickly!");
        set("revealedPlatformDialogue", true);
    }

    public void spawnCloud(Entity crate) {
        PropertyMap cloudPosition = crate.getProperties();
            Entity e = getGameWorld().create("revealedPlatform1", new SpawnData(cloudPosition.getInt("cloudX"),
                    cloudPosition.getInt("cloudY")));
            spawnWithScale(e, Duration.seconds(.8), Interpolators.SMOOTH.EASE_IN());
            spawn("crateAnimation", crate.getX(), crate.getY());
            crate.removeFromWorld();
            inc("keys", -1);
            if (!getb("revealedPlatformDialogue")) {
                messages(crate.getX(), crate.getY());
        }
    }
}
