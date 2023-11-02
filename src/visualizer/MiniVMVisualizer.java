package visualizer;

import hardware.cpu.CPU;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class MiniVMVisualizer extends JFrame {

    public MiniVMVisualizer(SystemCall systemCall, CPU cpu) {
        // set attributes
        setTitle("MiniVM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        // add components
        JPanel dashBoard = new JPanel();
        dashBoard.setLayout(new GridLayout(1, 7));
        Console cpuContext = new Console(Color.BLACK);
        cpuContext.setText(cpu.getContext().toString());
        dashBoard.add(cpuContext);
        dashBoard.add(new Console(Color.BLACK));
        dashBoard.add(new Console(Color.BLACK));
        dashBoard.add(new Console(Color.BLACK));
        dashBoard.add(new Console(Color.BLACK));
        dashBoard.add(new Console(Color.BLACK));
        dashBoard.add(new Console(Color.BLACK));
        add(dashBoard);
        add(new Shell(systemCall), BorderLayout.SOUTH);
    }

}
