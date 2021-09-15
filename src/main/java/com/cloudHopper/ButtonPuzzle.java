package com.cloudHopper;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ButtonPuzzle extends Component {
    public static boolean button1 = false;
    public static boolean button2 = false;
    private boolean button3 = false;
    private boolean button4 = false;
    private boolean button5 = false;

    public static void puzzleTest(Entity button) {
        System.out.println(button.getProperties().getBoolean("pressed") + " button#: " + button.getProperties().getInt("buttonNumber"));
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

        if (button1 && button2) {
            System.out.println("Success!");
        } else if (!button1 && !button2){
            System.out.println("Fail!");
        }
    }
}
