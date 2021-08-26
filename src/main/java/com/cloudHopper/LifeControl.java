package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Node;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LifeControl extends Component {

    @Override
    public void onAdded() {
        for (int i = 0; i < geti("lives"); i++) {
            spawn("heart", 100+(32*i), getAppHeight()-60);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        for (int i = 0; i < geti("lives"); i++) {
            addUINode(addLife(), 100+(32*i), getAppHeight()-60);
        }
    }

    public Node addLife() {
        spawn("heart");
        return null;
    }

    public void removeLife() {

    }
}
