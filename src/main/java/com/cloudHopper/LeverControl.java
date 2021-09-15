package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class LeverControl extends Component {
    private StateComponent state;
    private AnimatedTexture texture;
    private AnimationChannel activatedLever, deactivatedLever;
    private LocalTimer leverTimer;

    public LeverControl() {
        activatedLever = new AnimationChannel(
                image("leverRight.png"),
                1,
                64,
                64,
                Duration.seconds(1),
                0,
                0);

        deactivatedLever = new AnimationChannel(
                image("leverLeft.png"),
                1,
                64,
                64,
                Duration.seconds(1),
                0,
                0);

        texture = new AnimatedTexture(deactivatedLever).loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        state = entity.getComponent(StateComponent.class);
        state.changeState(DEACTIVATED);
        leverTimer = newLocalTimer();
    }

    private final EntityState DEACTIVATED = new EntityState("DEACTIVATED") {
        @Override
        public void onEntering() {
            if (texture.getAnimationChannel() != deactivatedLever) {
                texture.loopAnimationChannel(deactivatedLever);
            }
        }
    };

    private final EntityState ACTIVATED = new EntityState("ACTIVATED") {
        @Override
        public void onEntering() {
            if (texture.getAnimationChannel() != activatedLever) {
                texture.loopAnimationChannel(activatedLever);
            }
            leverTimer.capture();
            spawn("block", new Point2D(10974, 64));
        }

        @Override
        public void onUpdate(double tpf) {
            if (leverTimer.elapsed(Duration.seconds(3))) {
                state.changeState(DEACTIVATED);
            }
        }
    };

    public void message() {
        if (!getb("leverDialogue")) {
            getNotificationService().pushNotification("Press E to pull Levers.");
            set("leverDialogue", true);
        }
    }

    public void activateLever() {
        state.changeState(ACTIVATED);
    }

    public void deactivateLever() {
        state.changeState(DEACTIVATED);
    }
}
