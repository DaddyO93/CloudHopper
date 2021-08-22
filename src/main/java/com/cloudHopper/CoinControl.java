package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

public class CoinControl extends Component {
    private AnimationChannel coinAnimation;
    private AnimatedTexture texture;

    public CoinControl() {
        coinAnimation = new AnimationChannel(
                image("coin-sprite-animation.png"),
                10,
                100,
                100,
                Duration.seconds(.5),
                0,
                9);
        texture = new AnimatedTexture((coinAnimation));
        texture.loop();
    }

    @Override
    public void onAdded() { entity.getViewComponent().addChild(texture);}
}
