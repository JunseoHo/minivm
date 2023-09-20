package main;

import view.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

}
