package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

//  Lives explanation:
    //  Damage:
        //  1 life = 1 heart
        //  1 hit drops heart to 1/2
        //  a second hit drops heart to empty
        //  a third hit removes the heart
    //  10 stars <automatically(?) or is purchased(?)> heals 1/2 heart
    //  30 stars can buy another heart at the store

public class HeartControl extends Component {
    private AnimatedTexture texture;
    private AnimationChannel heartAnimation;
    private int damage = 2; //  number represents image index

    public HeartControl() {
        heartAnimation = new AnimationChannel(
                image("hearts.png"),
                3,
                32,
                32,
                Duration.seconds(1),
                damage,
                damage);
        texture = new AnimatedTexture(heartAnimation).loop();
    }

    @Override
    public void onAdded() { entity.getViewComponent().addChild(texture);}

    @Override
    public void onUpdate(double tpf) {
        texture.loopAnimationChannel(heartAnimation);
    }

    private void messageFullHeart() {
        getNotificationService().pushNotification("You are fully healed.");
    }

    public void addLife() {

    }

    private void removeHeart() {

    }



    public void healDamage() {
        if (damage>2) {
            damage = 2;
            messageFullHeart();
        }
    }

    public void takeDamage() {
         if (damage<0) {
             damage = 0;
         }
    }
}
