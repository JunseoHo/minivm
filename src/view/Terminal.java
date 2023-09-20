package view;

import os.OperatingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class Terminal extends TextAreaPanel {

    private OperatingSystem operatingSystem;
    private Queue<Character> inputBuffer;

    public Terminal() {
        this.inputBuffer = new LinkedList<>();
        this.appendString("Welcome to Mini-OS\n");
        this.appendString("Project for System programming2 Myongji Univ. 2023-2 \n");
        this.textArea.addKeyListener(new TerminalKeyEventHandler());
    }

    public void associate(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    private void systemCall() {
        if (operatingSystem == null) {
            appendString("\nOperating system is not associated.");
            inputBuffer.clear();
            return;
        }
        String input = "";
        while (!inputBuffer.isEmpty()) input += inputBuffer.poll();
        String[] token = input.split(" ");
        if (token.length > 2) {
            appendString("\nInvalid input.");
            return;
        }
        switch (token[0]) {
            case "ls" -> System.out.println("ls");
            case "cd" -> System.out.println("cd");
            case "cat" -> System.out.println("cat");
            default -> appendString("\nUnknown command.");
        }
    }

    private class TerminalKeyEventHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) systemCall();
            else inputBuffer.add(e.getKeyChar());
        }
    }

}
