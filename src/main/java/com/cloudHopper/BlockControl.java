package com.cloudHopper;

import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BlockControl extends Component {
    public void message() {
        if (!getb("blockDialogue")){
            getNotificationService().pushNotification("You can push blocks.");
            set("blockDialogue", true);
        }
    }
}
