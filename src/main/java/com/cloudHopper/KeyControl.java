package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

public class KeyControl extends Component {
    private AnimatedTexture texture;
    private AnimationChannel keyAnimation;

    public KeyControl() {
        float randomSpeed = (float) (new Random().nextFloat()+.6);

        keyAnimation = new AnimationChannel(
                image("keyAnimation.png"),
                8,
                64,
                64,
                Duration.seconds(randomSpeed),
                0,
                7);

        texture = new AnimatedTexture(keyAnimation).loop();
    }

    @Override
    public void onAdded() { entity.getViewComponent().addChild(texture);}
}
