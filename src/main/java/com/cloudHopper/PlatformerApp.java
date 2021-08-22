package com.cloudHopper;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlatformerApp extends GameApplication {
    private Entity player;
    private Entity enemy;
    private Point2D startPosition = new Point2D(64, 988);
    private PhysicsComponent physics;


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1000);
        gameSettings.setHeight(800);
        gameSettings.setTitle("Cloud Hopper");
        gameSettings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.A, () -> player.getComponent(PlayerControl.class).left(player));
        onKey(KeyCode.D, () -> player.getComponent(PlayerControl.class).right(player));
        onKeyDown(KeyCode.SPACE, () -> player.getComponent(PlayerControl.class).jump());
        onKeyDown(KeyCode.W, () -> player.getComponent(PlayerControl.class).attack(player));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        super.initGameVars(vars);
        vars.put("score", 0);
        vars.put("keys", 0);
        vars.put("lives", 3);
        vars.put("invulnerable", false);
        vars.put("keyDialogue", false);
        vars.put("blockDialogue", false);
        vars.put("revealedPlatformDialogue", false);
        vars.put("enemyDialogue", false);
        vars.put("playerMovementSpeed", 256);
        vars.put("playerJumpDistance", 608);
        vars.put("enemyMovementSpeed", 100);
        vars.put("enemyDirection", "right");
        vars.put("stoneMovementSpeed", 700);
        vars.put("startPosition", startPosition);
        vars.put("maxY", 20 * 64);
        vars.put("maxX", 320*64);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());
        getGameScene().setBackgroundColor(Color.BLACK);
        setLevelFromMap("level1.tmx");

        player = spawn("player", startPosition);
        set("player", player);

        Viewport viewport = getGameScene().getViewport();
        /**
         * minX/minY = should be 0 to ensure screen does not go beyond left X/top Y
         * maxX & maxY = should be total width/height of map (number of blocks * pixels of each block)
         */
        viewport.setBounds(0, 0, geti("maxX"), geti("maxY"));
        viewport.bindToEntity(player, getAppWidth()/2, getAppHeight()/2);
        viewport.setLazy(true);

//        spawn("backgroundZ1");
        spawn("backgroundZ2");
        spawn("backgroundZ3");
        spawn("backgroundZ4");
//        spawn("clouds");
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 1100);

        onCollisionBegin(EntityType.PLAYER, EntityType.BLOCK, (player, block) -> {
            if (!getb("blockDialogue")){
                getDialogService().showMessageBox("Push blocks to help you get where you need.");
                set("blockDialogue", true);
            }
        });

//        onCollisionBegin(EntityType.PLAYER, EntityType.FLAG, (player, flag) -> {
//            getDialogService().showMessageBox("Level Complete!");
//        });

//        onCollisionBegin(EntityType.PLAYER, EntityType.BUTTON, (player, button) -> {
//            button.getComponent(ButtonComponent.class).activateButton();
//        });

        onCollisionBegin(EntityType.ENEMY, EntityType.PLAYER, (enemy, player) -> {
            playerLivesTest();
            player.getComponent(PlayerControl.class).invulnerabilityTest(player, enemy);
        });

        onCollisionBegin(EntityType.ENEMY, EntityType.WALL, (enemy, wall) -> {
            enemy.getComponent(EnemyAIComponent.class).directionChange();
        });

//        onCollisionBegin(EntityType.PLAYER, EntityType.COIN, (player, coin) -> {
//            spawn("scoreText", new SpawnData(coin.getX(), coin.getY()).put("text", "+100 Points!"));
//            coin.removeFromWorld();
//            inc("score", 100);
//        });

        onCollisionBegin(EntityType.PLAYER, EntityType.KEY, (player, key) -> {
            if (!getb("keyDialogue")) {
                getDialogService().showMessageBox("Use keys to unlock crates to release clouds.");
                set("keyDialogue", true);
            }
            spawn("keyDisappearAnimation", key.getX(), key.getY());
            inc("keys", 1);
            key.removeFromWorld();
        });

        onCollisionBegin(EntityType.PLAYER, EntityType.CRATE, (player, crate) -> {
            PropertyMap cloudPosition = crate.getProperties();
            if (geti("keys") > 0) {
                Entity e = getGameWorld().create("revealedPlatform1", new SpawnData(cloudPosition.getInt("cloudX"),
                        cloudPosition.getInt("cloudY")));
                spawnWithScale(e, Duration.seconds(.8), Interpolators.SMOOTH.EASE_IN());
                spawn("crateAnimation", crate.getX(), crate.getY());
                crate.removeFromWorld();
                inc("keys", -1);
                if (!getb("revealedPlatformDialogue")) {
                    getDialogService().showMessageBox("You freed a cloud! Move quickly, they don't stick around " +
                            "long!");
                    set("revealedPlatformDialogue", true);
                }
            }
        });

        onCollision(EntityType.STONE, EntityType.GROUND, (stone, ground) -> stone.removeFromWorld());
        onCollision(EntityType.STONE, EntityType.WALL, (stone, wall) -> stone.removeFromWorld());

        onCollision(EntityType.STONE, EntityType.ENEMY, (stone, enemy) -> {
            enemy.removeFromWorld();
            stone.removeFromWorld();
            spawn("scoreText", new SpawnData(enemy.getX(), enemy.getY()).put("text", "+200 Points!"));
            inc("score", +200);
        });
    }

    public void playerLivesTest() {
        if (geti("lives")>1) {
            inc("lives", -1);
        } else {
            gameOverDialogue();
        }
    }

    public void gameOverDialogue() {
        getDialogService().showConfirmationBox("Game Over. Play Again?", this::playAgain);
    }

    public void playAgain(boolean answer) {
        if (!answer) {
            showMessage("Thanks for playing!", getGameController()::exit);
        } else {
            getGameController().startNewGame();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        var scoreText = getUIFactoryService().newText("", 24);
        scoreText.textProperty().bind(getip("score").asString("Score: %d"));
        getWorldProperties().addListener("score", (prev, now) -> {
            animationBuilder()
                    .duration(Duration.seconds(.5))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(scoreText)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });

        var keyText = getUIFactoryService().newText("", 24);
        keyText.textProperty().bind(getip("keys").asString("Keys: %d"));
        getWorldProperties().addListener("keys", (prev, now) -> {
            animationBuilder()
                    .duration(Duration.seconds(.5))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(keyText)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });

        var livesText = getUIFactoryService().newText("", 24);
        livesText.textProperty().bind(getip("lives").asString("Lives: %d"));
        getWorldProperties().addListener("lives", (prev, now) -> {
            animationBuilder()
                    .duration(Duration.seconds(.5))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(livesText)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });

        addUINode(livesText, 50, getAppHeight()-60);
        addUINode(scoreText, 50, getAppHeight()-20);
        addUINode(keyText, 300, getAppHeight()-20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
