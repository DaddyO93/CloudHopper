package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.image;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class ButtonComponent extends Component {
    private StateComponent state;
    private AnimatedTexture texture;
    private AnimationChannel activeButton, inactiveButton;
    public Entity block;
    private boolean blockDispensed = false;
    private ButtonPuzzle buttonPuzzle = new ButtonPuzzle();

    public ButtonComponent() {
        activeButton = new AnimationChannel(
                image("button_pressed.png"),
                1,
                64,
                64,
                Duration.seconds(1),
                0,
                0);

        inactiveButton = new AnimationChannel(
                image("button.png"),
                1,
                64,
                64,
                Duration.seconds(1),
                0,
                0);
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
            entity.getProperties().setValue("pressed", true);
        }

        @Override
        public void onUpdate(double tpf) {
            if (block != null) {
                if (texture.getAnimationChannel() != activeButton)
                    texture.loopAnimationChannel(activeButton);
                if (!blockDispensed) {
                    spawn("block", new Point2D(11100, 64));
                    blockDispensed = true;
                }

                if (!entity.isColliding(block)) {
                    state.changeState(INACTIVE);
                    blockDispensed = false;
                }
            }
        }
    };

    private final EntityState INACTIVE = new EntityState("INACTIVE") {
        @Override
        public void onUpdate(double tpf) {
            if (texture.getAnimationChannel() != inactiveButton)
                texture.loopAnimationChannel(inactiveButton);
            block = null;
            entity.getProperties().setValue("pressed", false);

        }
    };

    public void activateButton() {
        state.changeState(ACTIVE);
         ButtonPuzzle.puzzleTest(entity);
//        puzzleTest();
    }

//    private void puzzleTest() {
////        System.out.println(entity.getProperties().getInt("buttonNumber"));
//        if (entity.getProperties().getInt("buttonNumber") == 1 && !button1) {
////            System.out.println(entity.getProperties().getInt("buttonNumber"));
//            button1 = true;
//        } else if (entity.getProperties().getInt("buttonNumber") == 2 && !button2) {
////            System.out.println(entity.getProperties().getInt("buttonNumber"));
//            button2 = true;
//        } else if (button1 && button2) {
//            System.out.println("Success!");
//        }
//        System.out.println(entity.getProperties().getInt("buttonNumber"));
////        System.out.println(button2 + " button2");
//    }
}
