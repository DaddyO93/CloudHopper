package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PlayerControl extends Component {

    private PhysicsComponent physics;
    private final double movementSpeedFinal = geti("playerMovementSpeed");
    private double movementSpeed = movementSpeedFinal;
    private Integer jumpDistance = geti("playerJumpDistance");
    private LocalTimer invulnerabilityTimer;
    private LocalTimer attackTimer;
    private StateComponent state;
    public Entity touchingBlock;
    private Double facing;

    private Point2D startPosition = geto("startPosition");

    private AnimatedTexture texture;
    private AnimationChannel animationIde, animationWalk, animationJump, animationPush, animationPushIdle;

    public PlayerControl() {
        animationIde = new AnimationChannel(
                image("single.png"),
                4,
                60,
                64,
                Duration.seconds(.5),
                1,
                1);

        animationWalk = new AnimationChannel(
                image("single.png"),
                4,
                60,
                64,
                Duration.seconds(.5),
                0,
                3);

        animationJump = new AnimationChannel(
                image("single.png"),
                4,
                60,
                64,
                Duration.seconds(.5),
                0,
                0);

        animationPush = new AnimationChannel(
                image("singlePushing.png"),
                4,
                60,
                64,
                Duration.seconds(.5),
                0,
                3);

        animationPushIdle = new AnimationChannel(
                image("singlePushing.png"),
                4,
                60,
                64,
                Duration.seconds(.5),
                3,
                3);

        //  this is the default animation
        texture = new AnimatedTexture(animationIde).loop();
    }

    @Override
    public void onAdded() {
        attackTimer = newLocalTimer();
        entity.getViewComponent().addChild(texture);
        state = entity.getComponent(StateComponent.class);
        state.changeState(NORMAL);
    }

    @Override
    public void onUpdate(double tpf) {
        if ((getb("pushingBlock") || (touchingBlock != null && entity.distanceBBox(touchingBlock) < 10)) && physics.isMovingX()) {
            if (texture.getAnimationChannel() != animationPush) {
                texture.loopAnimationChannel(animationPush);
            }
        } else if (getb("pushingBlock")) {
            if (texture.getAnimationChannel() != animationPushIdle) {
                texture.loopAnimationChannel(animationPushIdle);
            }
        }
        else if (physics.isMovingY()) {
            if (texture.getAnimationChannel() != animationJump) {
                texture.loopAnimationChannel(animationJump);
            }
        } else if (physics.isMovingX()) {
            if (texture.getAnimationChannel() != animationWalk) {
                texture.loopAnimationChannel(animationWalk);
            }
        } else {
            if (texture.getAnimationChannel() != animationIde) {
            texture.loopAnimationChannel(animationIde);
            }
        }

        if (entity.getY() > geti("maxY")) {
            HeartControl.takeDamage();
            restart(entity);
        }
    }

    private final EntityState NORMAL = new EntityState("NORMAL") {
        @Override
        public void onEntering() {
            set("pushingBlock", false);
            touchingBlock = null;
            movementSpeed = movementSpeedFinal;
        }
    };

    private final EntityState INVULNERABLE = new EntityState("INVULNERABLE") {
        @Override
        public void onEntering() {
            invulnerabilityTimer = newLocalTimer();
            invulnerabilityTimer.capture();
            entity.getComponent(CollidableComponent.class).addIgnoredType(EntityType.ENEMY);
            knockBack(entity);
        }

        @Override
        public void onUpdate(double tpf) {
            if (invulnerabilityTimer.elapsed(Duration.seconds(5))) {
                entity.getComponent(CollidableComponent.class).removeIgnoredType(EntityType.ENEMY);
                state.changeState(NORMAL);
            }
        }
    };

    private final EntityState PULLING = new EntityState("PULLING") {
        @Override
        public void onUpdate(double tpf) {
            if (touchingBlock != null && getb("pushingBlock")) {
                determineFacing();
                if (entity.distanceBBox(touchingBlock) < 10) {
                    if (touchingBlock.getY() < entity.getY())
                        touchingBlock.translateX(20);
                    entity.setScaleX(facing);
                    movementSpeed = 120;
                    double pushingSpeed = entity.getComponent(PhysicsComponent.class).getVelocityX();
                    touchingBlock.getComponent(BlockControl.class).moveBlock(pushingSpeed);
                } else
                    state.changeState(NORMAL);
            } else state.changeState(NORMAL);
        }
    };

    public void left() {
        if (entity.getX()>40) {
            move((int) -movementSpeed);
            entity.setScaleX(-1);
        } else {
            move((int) movementSpeed);
        }
    }

    public void right() {
        if (entity.getX()<(geti("maxX")-40)) {
            move((int) movementSpeed);
            entity.setScaleX(1);
        } else {
            move((int) -movementSpeed);
        }
    }

    public void jumpTest() {
        if (!physics.isMovingY()){
            changeToNormalState();
            jump(jumpDistance);
        }
    }

    private void jump(int distance) {
            physics.setVelocityY(-distance);
    }

    public void attack(Entity player) {
        if (attackTimer.elapsed(Duration.seconds(.3))) {
            spawn("stone", player.getX() + 20, player.getY());
            attackTimer.capture();
        }
    }

    private void determineFacing () {
        if (touchingBlock != null) {
            if (entity.getX() < touchingBlock.getX())
                facing = 1.0;
            else {
                facing = -1.0;
            }
        }
    }

    public void moveBlock() {
        state.changeState(PULLING);
    }

    public void move(int distance) {
        physics.setVelocityX(distance);
    }

    private void restart(Entity player) {
        player.getComponent(PhysicsComponent.class).overwritePosition(startPosition);
    }

    public void changeToInvulnerableState() {
        state.changeState(INVULNERABLE);
    }

    public void changeToNormalState() {
        state.changeState(NORMAL);
    }

    private void knockBack(Entity player) {
        move(400 * (int) (player.getScaleX()*-1));
        jump(600);
    }
}
