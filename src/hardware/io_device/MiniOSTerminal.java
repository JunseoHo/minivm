package hardware.io_device;

import os.SystemCall;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MiniOSTerminal extends MiniOSTextArea {

    // Associations
    private SystemCall systemCall;
    // Attributes
    private StringBuilder stringBuilder;


    public MiniOSTerminal(SystemCall systemCall) {
        this.systemCall = systemCall;
        textArea.append("MiniOS\n");
        stringBuilder = new StringBuilder();
        textArea.setEditable(true);
        textArea.addKeyListener(new MiniOSTerminalKeyHandler());
    }

    private void processCommand(String[] token) {
//        if (token.length > 2) textArea.append("\nInvalid arguments.");
//        else {
//            switch (token[0]) {
//                case "ls" -> append(systemCall.ls());
//                default -> append("Unknown command.");
//            }
//        }
    }

    private class MiniOSTerminalKeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                processCommand(stringBuilder.toString().split(" "));
                stringBuilder = new StringBuilder();
            } else stringBuilder.append(e.getKeyChar());
        }
    }

}
