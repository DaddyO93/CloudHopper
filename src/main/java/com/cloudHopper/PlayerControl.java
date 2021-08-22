package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
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

    private Point2D startPosition = geto("startPosition");

    private AnimatedTexture texture;
    private AnimationChannel animationIde, animationWalk;

    public PlayerControl() {
        animationIde = new AnimationChannel(
                image("player_idle.png"),
                3,
                70,
                100,
                Duration.seconds(.5),
                0,
                0);

        animationWalk = new AnimationChannel(
                image("player_walking.png"),
                3,
                70,
                100,
                Duration.seconds(.5),
                0,
                2);

        //  this is the default animation
        texture = new AnimatedTexture(animationIde).loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {

        if (physics.isMoving()) {
            if (texture.getAnimationChannel() != animationWalk) {
                texture.loopAnimationChannel(animationWalk);
            }
        } else {
            if (texture.getAnimationChannel() != animationIde) {
                texture.loopAnimationChannel(animationIde);
            }
        }

        if (entity.getY() > geti("maxY")) {
            new com.cloudHopper.PlatformerApp().playerLivesTest();
            restart(entity);
        }
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

    public void jump() {
        if (!physics.isMovingY()){
            jump(jumpDistance);
        }
    }

    public void attack(Entity player) {
        spawn("stone", player.getX()+35, player.getY());
    }

    private void restart(Entity player) {
        player.getComponent(PhysicsComponent.class).overwritePosition(startPosition);
    }

    public void invulnerabilityTest(Entity player, Entity enemy) {
        System.out.println("Invulnerable: " + getb("invulnerable"));
        if (!getb("invulnerable")) {
            invulnerability = newLocalTimer();
            invulnerability.capture();
            set("invulnerable", true);
            knockBack(player, enemy);
        } else {
            if (invulnerability.elapsed(Duration.seconds(4))) {
                set("invulnerable", false);
            }
        }
    }

    private void jump(int distance) {
        physics.setVelocityY(-distance);
    }

    private void move(int distance) {
        physics.setVelocityX(distance);
    }

    private void knockBack(Entity player, Entity enemy) {
        int knockbackDirection = 1;
        if (player.getX() < enemy.getX()) {
            knockbackDirection = -1;
        }
        move(400 * knockbackDirection);
        jump(600);
    }
}
