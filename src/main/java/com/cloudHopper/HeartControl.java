package com.cloudHopper;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.cloudHopper.LifeControl.hearts;

//  Hearts explanation:
    //  Damage:
        //  1 hit drops heart to 1/2
        //  a second hit drops heart to empty
    //  10 stars <automatically(?) or is purchased(?)> heals 1/2 heart
    //  30 stars can buy another heart at the store

public class HeartControl extends Component {
    public AnimatedTexture texture;
    public AnimationChannel fullHeart, halfHeart, emptyHeart;
    private int damage = 2; //  number represents image index

    public HeartControl() {
        fullHeart = new AnimationChannel(
                image("hearts.png"),
                3,
                32,
                32,
                Duration.seconds(1),
                2,
                2);

        halfHeart = new AnimationChannel(
                image("hearts.png"),
                3,
                32,
                32,
                Duration.seconds(1),
                1,
                1);

        emptyHeart = new AnimationChannel(
                image("hearts.png"),
                3,
                32,
                32,
                Duration.seconds(1),
                0,
                0);

        texture = new AnimatedTexture(fullHeart).loop();
    }

    @Override
    public void onAdded() {
        damage = entity.getComponent(HealthIntComponent.class).getValue();
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
//        damage = entity.getComponent(HealthIntComponent.class).getValue();
        if (entity.getComponent(HealthIntComponent.class).getValue() == 2) {
            if (texture.getAnimationChannel() != fullHeart) {
                texture.loopAnimationChannel(fullHeart);
            }
        } else if (entity.getComponent(HealthIntComponent.class).getValue() == 1) {
            if (texture.getAnimationChannel() != halfHeart) {
                texture.loopAnimationChannel(halfHeart);
            }
        } else if (entity.getComponent(HealthIntComponent.class).getValue() == 0) {
            if (texture.getAnimationChannel() != emptyHeart) {
                texture.loopAnimationChannel(emptyHeart);
            }
        }

        if (geti("stars") > 10)
            healDamage();
    }

    private void messageFullHeart() {
        getNotificationService().pushNotification("You are fully healed.");
    }

    public void addHeart() {
    }

    public void healDamage() {
        for (int counter = 0; counter < hearts.size(); counter++) {
            Entity singleHeart = (Entity) hearts.get(counter);
            if (singleHeart.getComponent(HealthIntComponent.class).getValue() < 2) {
                int currentHealth = singleHeart.getComponent(HealthIntComponent.class).getValue();
                singleHeart.getComponent(HealthIntComponent.class).setValue(currentHealth+1);
                inc("stars", -1);
                return;
            }
        }
    }

    public static void takeDamage() {
        for (int counter = hearts.size(); counter > 0; counter--) {
            Entity singleHeart = (Entity) hearts.get(counter-1);
            if (singleHeart.getComponent(HealthIntComponent.class).getValue() > 0) {
                int currentHealth = singleHeart.getComponent(HealthIntComponent.class).getValue();
                singleHeart.getComponent(HealthIntComponent.class).setValue(currentHealth-1);
                return;
            }
        }
        new PlatformerApp().gameOverDialogue();
    }
}
