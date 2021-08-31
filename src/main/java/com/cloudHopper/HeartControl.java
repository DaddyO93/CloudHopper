package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getNotificationService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

//  Lives explanation:
    //  Damage:
        //  1 life = 1 heart
        //  1 hit drops heart to 1/2
        //  a second hit drops heart to empty
    //  10 stars <automatically(?) or is purchased(?)> heals 1/2 heart
    //  30 stars can buy another heart at the store

public class HeartControl extends Component {
    public AnimatedTexture texture;
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
//        spawn("heart").setLocalAnchor(100+(32*i), getAppHeight()-60);
        for (int i = 0; i < geti("lives"); i++) {
            Point2D location = new Point2D(100 + (32 * i), getAppHeight() - 60);
            spawn("heart").setLocalAnchor(location);
        }
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
        damage --;
         if (damage<0) {
             damage = 2;
             inc("lives", -1);
             new PlayerControl().livesTest();
         }
        System.out.println(damage);
    }
}
