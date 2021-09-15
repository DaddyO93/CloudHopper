package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ButtonPuzzle extends Component {
    private boolean button1 = false;
    private boolean button2 = false;
    private boolean button3 = false;
    private boolean button5 = false;

    public void puzzleTest(Entity button) {
        if (button.getProperties().getInt("buttonNumber") == 1 && button.getProperties().getBoolean("pressed")) {
            button1 = true;
        } else if (button.getProperties().getInt("buttonNumber") == 1 && !button.getProperties().getBoolean("pressed")) {
            button1 = false;
        }

        if (button.getProperties().getInt("buttonNumber") == 2 && button.getProperties().getBoolean("pressed")) {
            button2 = true;
        } else if (button.getProperties().getInt("buttonNumber") == 2 && !button.getProperties().getBoolean("pressed")) {
            button2 = false;
        }

        if (button.getProperties().getInt("buttonNumber") == 3 && button.getProperties().getBoolean("pressed")) {
            button3 = true;
        } else if (button.getProperties().getInt("buttonNumber") == 3 && !button.getProperties().getBoolean("pressed")) {
            button3 = false;
        }

        if (button.getProperties().getInt("buttonNumber") == 5 && button.getProperties().getBoolean("pressed")) {
            button5 = true;
        } else if (button.getProperties().getInt("buttonNumber") == 5 && !button.getProperties().getBoolean("pressed")) {
            button5 = false;
        }

        if (button1 && button2 && button3 && button5) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail!");
        }
    }
}
