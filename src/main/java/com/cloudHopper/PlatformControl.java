package com.cloudHopper;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PlatformControl extends Component {
    private StateComponent state;
    private LocalTimer cloudTimer;

    @Override
    public void onAdded() {
        state = entity.getComponent(StateComponent.class);
        cloudTimer = FXGL.newLocalTimer();
        cloudTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (cloudTimer.elapsed(Duration.seconds(5))) {
            state.changeState(REMOVED);
        }
    }

    private final EntityState REMOVED = new EntityState("REMOVED") {
        @Override
        public void onEntering() {
            spawn("removePlatform", new SpawnData(entity.getX(), entity.getY()));
            entity.removeFromWorld();
        }
    };
}
