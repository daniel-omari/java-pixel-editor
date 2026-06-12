package com.danielomari.pixeleditor.util;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class AutomaticSave {
    private Timer timer;

    public AutomaticSave() {
        startAutoSave();
    }

    private void startAutoSave() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoSave();
            }
        }, 0, 300000); // 60000 milliseconds = 1 minute
        System.out.println("Automatic Save Started");
    }

    public void stopAutoSave() {
        if (timer != null) {
            timer.cancel();
            System.out.println("Automatic Save Stopped");
        }
    }

    // Using a timer to automatically trigger a save of the file every x minutes,
    // checking it has been saved beforehand before issuing the save.
    private void autoSave() {
//         Save save = new Save();
        if (Save.hasSaved()) {
            Save.saveImage();
            System.out.println("Automatic Save Saved");
            return;
        } else {
            System.out.println("No previously saved file to save (Skipping)");
        }
    }


}
