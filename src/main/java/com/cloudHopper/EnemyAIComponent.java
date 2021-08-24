package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

public class EnemyAIComponent extends Component {

    private final int patrolSpeed = 100;
    private final int attackSpeed = 130;
    private int speed = patrolSpeed;
    private EntityState nextState;
    private StateComponent state;
    private Entity player;
    private PhysicsComponent physics;
    private LocalTimer jumpTimer;

    private AnimatedTexture texture;
    private AnimationChannel animationWalk;

    public EnemyAIComponent() {
        animationWalk = new AnimationChannel(
                image("characters_packed.png"),
                9,
                48,
                48,
                Duration.seconds(.5),
                15,
                17);
        texture = new AnimatedTexture(animationWalk).loop();
    }

    @Override
    public void onAdded() {
        state = entity.getComponent(StateComponent.class);
        state.changeState(PATROL);
        jumpTimer = newLocalTimer();
        jumpTimer.capture();
        entity.getViewComponent().addChild(texture);
        physics.setBodyType(BodyType.DYNAMIC);
        entity.getComponent(CollidableComponent.class).addIgnoredType(EntityType.ENEMY);
    }

    @Override
    public void onUpdate(double tpf) {
        player = getGameWorld().getSingleton(com.cloudHopper.EntityType.PLAYER);

        if (entity.distance(player) < getAppWidth()/2 && !getb("enemyDialogue"))  {
            getDialogService().showMessageBox("Watch out for that! Press W to throw a rock!");
            set("enemyDialogue", true);
        }

        if (entity.distance(player) < 300) {
            state.changeState(ATTACK);
        }
        else {
            state.changeState(PATROL);
        }
    }

    private final EntityState PATROL = new EntityState("PATROL") {
        private String direction = "left";

        @Override
        public void onEntering() {
            speed = patrolSpeed;
        }

        @Override
        public void onEnteredFrom(EntityState previousState) {
            if (nextState == ATTACK)
                speed = patrolSpeed;
        }

        @Override
        protected void onUpdate(double tpf) {
            if (gets("enemyDirection").equals("right")) {
                // move entity right
                physics.setVelocityX(speed);
                entity.setScaleX(-1);
//                direction = "right";
            } else if (gets("enemyDirection").equals("left")) {
                //  move entity left
                physics.setVelocityX(-speed);
                entity.setScaleX(1);
//                direction = "left";
            }
        }
    };

    private final EntityState ATTACK = new EntityState("ATTACK") {
        private String direction = "left";

        @Override
        public void onEntering() {
            speed = attackSpeed;
        }

        @Override
        protected void onUpdate(double tpf) {

            if (jumpTimer.elapsed(Duration.seconds(1.5))) {
                jump();
                jumpTimer.capture();
            }

            if (entity.getX() < player.getX() && direction.equals("left")) {
                // move entity right
                physics.setVelocityX(speed);
                direction = "right";
            } else if (entity.getX() > player.getX() && direction.equals("right")) {
                //  move entity left
                physics.setVelocityX(-speed);
                direction = "left";
            } else if (direction.equals("left")) {
                //  move entity left
                physics.setVelocityX(-speed);
            } else {
                //  move entity right
                physics.setVelocityX(speed);
            }
        }
    };

    private void jump() {
        Integer facing = 1;
        if (entity.getX()<player.getX()) {
            facing *= -1;
        }
        entity.setScaleX(facing);
        physics.setVelocityY(-475);
    }

    public void directionChange() {
        if (state.getCurrentState() == PATROL) {
            if (gets("enemyDirection").equals("right")) {
                physics.setVelocityX(-20);
                set("enemyDirection", "left");
            } else  {
                physics.setVelocityX(20);
                set("enemyDirection", "right");
            }
        }
    }

    public void blockOnHead(Entity enemy, Entity block) {
        if (block.getY()+60 < enemy.getY()) {
            killEnemy(enemy);
        }
    }

    public void killEnemy(Entity enemy) {
        enemy.removeFromWorld();
        spawn("scoreText", new SpawnData(enemy.getX(), enemy.getY()).put("text", "+200 Points!"));
        inc("score", +200);
    }

//    private void edgeDetection() {
//        Point2D enemyTempLocation = entity.getPosition();
//        physics.setVelocityX(speed);
//        physics.addSensor(g);
//        if (!enemyTempLocation.)
//    }
}
