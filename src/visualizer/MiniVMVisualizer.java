package visualizer;

import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class MiniVMVisualizer extends JFrame {

    public MiniVMVisualizer(SystemCall systemCall) {
        // set attributes
        setTitle("MiniVM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        // add components
        add(new Shell(systemCall), BorderLayout.SOUTH);
    }

}
