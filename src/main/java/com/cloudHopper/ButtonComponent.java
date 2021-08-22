package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

public class ButtonComponent extends Component {
    private StateComponent state;
    private AnimatedTexture texture;
    private AnimationChannel pressedButton, inactiveButton;

    public ButtonComponent() {
        pressedButton = new AnimationChannel(
                image("tiles_packed.png"),
                20,
                128,
                128,
                Duration.seconds(1),
                149,
                149);

        inactiveButton = new AnimationChannel(
                image("tiles_packed.png"),
                20,
                128,
                128,
                Duration.seconds(1),
                148,
                148);
        texture = new AnimatedTexture(inactiveButton).loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        state = entity.getComponent(StateComponent.class);
        state.changeState(INACTIVE);
    }

    private final EntityState ACTIVE = new EntityState("ACTIVE") {
        @Override
        public void onEntering() {
            texture.loopAnimationChannel(pressedButton);
        }
    };

    private final EntityState INACTIVE = new EntityState("INACTIVE") {
        @Override
        public void onEntering() {
            texture.loopAnimationChannel(inactiveButton);
        }
    };

    public void activateButton() {
        state.changeState(ACTIVE);
    }
}
