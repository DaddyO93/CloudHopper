package com.cloudHopper;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;

public class LockedBoxComponent extends Component {
    private Entity player;
    private EntityState nextState;
    private StateComponent state;

    @Override
    public void onAdded() {
        state = entity.getComponent(StateComponent.class);
        player = FXGL.getGameWorld().getEntitiesByType(com.cloudHopper.EntityType.PLAYER).get(0);
    }



}
