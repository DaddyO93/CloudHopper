package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
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
    private Integer movementSpeed = geti("playerMovementSpeed");
    private Integer jumpDistance = geti("playerJumpDistance");
    private LocalTimer invulnerability;
    private Boolean invulnerabilityTimer = false;
    private LocalTimer attackTimer;

    private Point2D startPosition = geto("startPosition");

    private AnimatedTexture texture;
    private AnimationChannel animationIde, animationWalk, animationJump, animationPush;

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

        //  this is the default animation
        texture = new AnimatedTexture(animationIde).loop();
    }

    @Override
    public void onAdded() {
        attackTimer = newLocalTimer();
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (getb("pushingBlock") && physics.isMoving()) {
            if (texture.getAnimationChannel() != animationPush) {
                texture.loopAnimationChannel(animationPush);
            }
        } else if (physics.isMovingY()) {
            if (texture.getAnimationChannel() != animationJump) {
                texture.loopAnimationChannel(animationJump);
            }
        } else if (physics.isMovingX()) {
            if (texture.getAnimationChannel() != animationWalk) {
                texture.loopAnimationChannel(animationWalk);
            }
        } else {if (texture.getAnimationChannel() != animationIde) {
                texture.loopAnimationChannel(animationIde);
            }
        }

        if (entity.getY() > geti("maxY")) {
            HeartControl.takeDamage();
//            heartsTest();
            restart(entity);
        }

        if (invulnerabilityTimer)
            invulnerabilityTest(entity);

        set("pushingBlock", false);
    }

    public void left(Entity player) {
        if (player.getX()>40) {
            move(-movementSpeed);
            player.setScaleX(-1);
        } else {
            move(movementSpeed);
        }
    }

    public void right(Entity player) {
        if (player.getX()<(geti("maxX")-40)) {
            move(movementSpeed);
            player.setScaleX(1);
        } else {
            move(-movementSpeed);
        }
    }

    public void jumpTest() {
        if (!physics.isMovingY()){
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

    public void move(int distance) {
        physics.setVelocityX(distance);
    }

    public void wallTest(Entity player, Entity wall) {
        int facing = 1;
        if (player.getX() > wall.getX()) {
            facing = -1;
        }
        move(20 * facing);
    }

    private void restart(Entity player) {
        player.getComponent(PhysicsComponent.class).overwritePosition(startPosition);
    }

    public void invulnerabilityTest(Entity player) {
        if (!getb("invulnerable")) {
            invulnerability = newLocalTimer();
            invulnerability.capture();
            player.getComponent(CollidableComponent.class).addIgnoredType(EntityType.ENEMY);
            set("invulnerable", true);
            invulnerabilityTimer = true;
            knockBack(player);
        } else {
            if (invulnerability.elapsed(Duration.seconds(4))) {
                player.getComponent(CollidableComponent.class).removeIgnoredType(EntityType.ENEMY);
                set("invulnerable", false);
                invulnerabilityTimer = false;
            }
        }
    }

    private void knockBack(Entity player) {
        move(400 * (int) (player.getScaleX()*-1));
        jump(600);
    }
}
