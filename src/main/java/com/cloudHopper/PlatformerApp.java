package com.cloudHopper;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    protected void initGameVars(Map<String, Object> vars) {
        super.initGameVars(vars);
        vars.put("score", 0);
        vars.put("keys", 0);
        vars.put("stars", 0);
        vars.put("lives", 1);
        vars.put("invulnerable", false);
        vars.put("keyDialogue", false);
        vars.put("starDialogue", false);
        vars.put("blockDialogue", false);
        vars.put("revealedPlatformDialogue", false);
        vars.put("enemyDialogue", false);
        vars.put("playerMovementSpeed", 256);
        vars.put("playerJumpDistance", 608);
        vars.put("pushingBlock", false);
        vars.put("enemyMovementSpeed", 100);
        vars.put("enemyDirection", "right");
        vars.put("stoneMovementSpeed", 700);
        vars.put("startPosition", startPosition);
        vars.put("maxY", 20 * 64);
        vars.put("maxX", 320*64);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.A, () -> player.getComponent(PlayerControl.class).left(player));
        onKey(KeyCode.D, () -> player.getComponent(PlayerControl.class).right(player));
        onKeyDown(KeyCode.SPACE, () -> player.getComponent(PlayerControl.class).jumpTest());
        onKeyDown(KeyCode.W, () -> player.getComponent(PlayerControl.class).attack(player));
//        onKeyDown(KeyCode.S, () -> player.getComponent(PlayerControl.class).squat());
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());
        getGameScene().setBackgroundColor(Color.ANTIQUEWHITE);
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

//        spawn("backgroundZ8");
//        spawn("backgroundZ7");
//        spawn("backgroundZ6");
//        spawn("backgroundZ5");
//        spawn("backgroundZ4");
//        spawn("backgroundZ3");
        spawn("backgroundZ2");
        spawn("backgroundZ1");
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 1100);

        onCollisionBegin(EntityType.ENEMY, EntityType.PLAYER, (enemy, player) -> {
            player.getComponent(PlayerControl.class).livesTest();
            player.getComponent(PlayerControl.class).invulnerabilityTest(player);
        });
        onCollisionBegin(EntityType.ENEMY, EntityType.WALL, (enemy, wall) -> {
            enemy.getComponent(EnemyAIComponent.class).directionChange();
        });
        onCollisionBegin(EntityType.ENEMY, EntityType.BLOCK, (enemy, block) -> {
            enemy.getComponent(EnemyAIComponent.class).blockOnHead(enemy, block);
        });

//        onCollision(EntityType.PLAYER, EntityType.BLOCK, (player, block) -> {
//            System.out.println((player.getX()));
//
//            block.getComponent(BlockControl.class).message();
//            set("pushingBlock", true);
//        });
        onCollision(EntityType.PLAYER, EntityType.BLOCK, (player, block) -> {
            set("pushingBlock", true);
        });
        onCollisionBegin(EntityType.PLAYER, EntityType.WALL, (player, wall) -> {
            player.getComponent(PlayerControl.class).wallTest(player, wall);
        });
        onCollisionBegin(EntityType.PLAYER, EntityType.KEY, (player, key) -> {
            key.getComponent(KeyControl.class).spawnDisappearingKey(key);
        });
        onCollisionBegin(EntityType.PLAYER, EntityType.CRATE, (player, crate) -> {
            if (geti("keys") > 0) {
                crate.getComponent(CrateControl.class).spawnCloud(crate);
            }
        });
        onCollisionBegin(EntityType.PLAYER, EntityType.STAR, (player, star) -> {
            star.getComponent(StarControl.class).spawnDisappearingStar(star);
        });

//        onCollisionBegin(EntityType.PLAYER, EntityType.FLAG, (player, flag) -> {
//            getDialogService().showMessageBox("Level Complete!");
//        });

//        onCollisionBegin(EntityType.PLAYER, EntityType.BUTTON, (player, button) -> {
//            button.getComponent(ButtonComponent.class).activateButton();
//        });

        onCollision(EntityType.STONE, EntityType.GROUND, (stone, ground) -> stone.removeFromWorld());
        onCollision(EntityType.STONE, EntityType.WALL, (stone, wall) -> stone.removeFromWorld());
        onCollision(EntityType.STONE, EntityType.BLOCK, (stone, block) -> stone.removeFromWorld());
        onCollision(EntityType.STONE, EntityType.ENEMY, (stone, enemy) -> {
            stone.getComponent(StoneControl.class).hitEnemy(stone, enemy);
        });
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

        Font fontGame = getUIFactoryService().newFont(FontType.GAME, 24.0);

//        var scoreText = getUIFactoryService().newText("", 24);
        Text scoreText = new Text("");
        scoreText.setFont(fontGame);
        scoreText.setFill(Color.WHITE);
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

//        var keyText = getUIFactoryService().newText("", 24);
        Text keyText = new Text("");
        keyText.setFont(fontGame);
        keyText.setFill(Color.WHITE);
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



        Text livesText = new Text("");
        livesText.setFont(fontGame);
        livesText.setFill(Color.WHITE);
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
