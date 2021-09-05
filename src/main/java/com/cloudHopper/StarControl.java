package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class StarControl extends Component {
    private AnimatedTexture texture;
    private AnimationChannel starAnimation;

    public StarControl() {
        float randomSpeed = (float) (new Random().nextFloat()+.6);

        starAnimation = new AnimationChannel(
                image("starAnimation.png"),
        14,
        50,
        50,
                Duration.seconds(randomSpeed),
        0,
        13);

        texture = new AnimatedTexture(starAnimation).loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    public void message() {
        getNotificationService().pushNotification("Collect stars to heal hearts.");
        set("starDialogue", true);
    }

    public void spawnDisappearingStar(Entity star) {
        if (!getb("starDialogue")) {
            message();
        }
        spawn("starDisappearingAnimation", star.getX(), star.getY());
        inc("stars", 1);
        star.removeFromWorld();
    }
}
