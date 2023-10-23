package visualizer;

import os.SystemCall;

import javax.swing.*;

public class InterruptButton extends JButton {

    private SystemCall systemCall;
    private InterruptGenerator generator;


    public InterruptButton(String intrName, SystemCall systemCall, InterruptGenerator generator) {
        super(intrName);
        this.systemCall = systemCall;
        this.generator = generator;
        addActionListener((e) -> generateInterrupt());
    }

    public void generateInterrupt() {
        systemCall.generateIntr(generator.generateInterrupt());
    }

}
