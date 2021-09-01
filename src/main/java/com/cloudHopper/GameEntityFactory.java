package com.cloudHopper;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;
import static com.cloudHopper.EntityType.*;

public class GameEntityFactory implements EntityFactory {

    @Spawns("backgroundZ1")
    public Entity newBackgroundZ1(SpawnData data) {
        return entityBuilder()
                .view(new ScrollingBackgroundView(image("z-1.png"), getAppWidth(), geti("maxY"),
                        Orientation.HORIZONTAL,
                        (geti("playerMovementSpeed")*.0002)+1))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .buildAndAttach();
    }

    @Spawns("backgroundZ2")
    public Entity newBackgroundZ2(SpawnData data) {
        return entityBuilder()
                .view(new ScrollingBackgroundView(image("z-2.png"), getAppWidth(), geti("maxY"),
                        Orientation.HORIZONTAL,
                        (geti("playerMovementSpeed")*.00008)+1))
                .zIndex(-2)
                .with(new IrremovableComponent())
                .buildAndAttach();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().friction(4.0f));

        return entityBuilder(data)
                .type(PLAYER)
//                .bbox(new HitBox(BoundingShape.box(50, 100)))   //  current width/height of sprite
                .bbox(new HitBox(BoundingShape.box(40, 64)))   //  current width/height of sprite
                .with(physics)
                .with(new PlayerControl())
                .collidable()
                .build();
    }

    @Spawns("stone")
    public Entity newStone(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(data)
                .type(STONE)
                .viewWithBBox("stone.png")
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new StoneControl())
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        return entityBuilder(data)
                .type(ENEMY)
                .bbox(new HitBox(BoundingShape.box(48, 48)))
                .with(new PhysicsComponent())
                .with(new StateComponent())
                .with(new CollidableComponent(true))
                .with(new EnemyAIComponent())
                .build();
    }

    @Spawns("ground")
    public Entity newGround(SpawnData data) {

        return entityBuilder(data)
                .type(GROUND)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        return entityBuilder(data)
                .type(WALL)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("revealedPlatform1")
    public Entity newRevealedPlatform1(SpawnData data) {
        return entityBuilder(data)
                .type(REVEALEDPLATFORM1)
                .viewWithBBox("floatingPlatform.png")
                .at(data.getX(), data.getY())
                .with(new StateComponent())
                .with(new RevealedPlatformControl())
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("removePlatform")
    public Entity newRemovePlatform(SpawnData data) {
        var e = entityBuilder(data)
                .view("floatingPlatform.png")
                .with(new ExpireCleanComponent(Duration.seconds(2)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.BACK.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY() - 30))
                .buildAndPlay();
        return e;
    }

    @Spawns("block")
    public Entity newBlock(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(18f));
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(data)
                .type(BLOCK)
                .viewWithBBox("StoneBlock.png")
                .with(physics)
                .with(new BlockControl())
                .collidable()
                .build();
    }

    @Spawns("star")
    public Entity newStar(SpawnData data) {
        return entityBuilder(data)
                .type(STAR)
                .bbox(new HitBox(BoundingShape.box(50, 50)))
                .with(new StarControl())
                .collidable()
                .build();
    }

    @Spawns("starDisappearingAnimation")
    public Entity newStarDisappearingAnimation(SpawnData data) {
        var e = entityBuilder(data)
                .view("star.png")
                .with(new ExpireCleanComponent(Duration.seconds(.8)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(.8))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY()-30))
                .buildAndPlay();

        return e;
    }

    @Spawns("key")
    public Entity newKey(SpawnData data) {
        return entityBuilder(data)
                .type(KEY)
                .bbox(new HitBox(BoundingShape.box(64, 64)))
                .with(new KeyControl())
                .collidable()
                .build();
    }

    @Spawns("keyDisappearAnimation")
    public Entity newKeyDisappearAnimation(SpawnData data) {
        var e = entityBuilder(data)
                .view("key.png")
                .with(new ExpireCleanComponent(Duration.seconds(.8)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(.8))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY() - 30))
                .buildAndPlay();
        return e;
    }

    @Spawns("crate")
    public Entity newCrate(SpawnData data) {
        return entityBuilder(data)
                .type(CRATE)
                .viewWithBBox("crate.png")
                .with(new CrateControl())
                .collidable()
                .build();
    }

    @Spawns("crateAnimation")
    public Entity newCrateAnimation(SpawnData data) {
        var e = entityBuilder(data)
                .view("crate.png")
                .with(new ExpireCleanComponent(Duration.seconds(.8)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(.8))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY() - 30))
                .buildAndPlay();
        return e;
    }

    @Spawns("heart")
    public Entity newHeart(SpawnData data) {
        return entityBuilder(data)
                .type(HEART)
                .view("hearts.png")
                .with(new HealthIntComponent(2))
                .with(new HeartControl())
                .build();
    }

//    @Spawns("flag")
//    public Entity newFlag(SpawnData data) {
//        return entityBuilder(data)
//                .type(FLAG)
//                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
//                .collidable()
//                .build();
//    }

    @Spawns("scoreText")
    public Entity newScoreText(SpawnData data) {
        String text = data.get("text");

        var e = entityBuilder(data)
                .view(getUIFactoryService().newText(text, 24))
                .with(new ExpireCleanComponent(Duration.seconds(.8)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(.8))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY() -30))
                .buildAndPlay();

        return e;
    }

//    @Spawns("button")
//    public Entity newButton(SpawnData data) {
//        return entityBuilder(data)
//                .type(BUTTON)
//                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
//                .with(new StateComponent())
//                .with(new ButtonComponent())
//                .collidable()
//                .build();
//    }
}
