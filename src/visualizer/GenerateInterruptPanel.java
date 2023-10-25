package visualizer;

import os.SIRQ;
import os.SWName;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class GenerateInterruptPanel extends JPanel {

    public GenerateInterruptPanel(SystemCall systemCall) {
        // set attributes
        setPreferredSize(new Dimension(250, 680));
        add(new InterruptButton("REQUEST_LOAD_PROCESS", systemCall, this::REQUEST_LOAD_PROCESS));
    }

    private SIRQ REQUEST_LOAD_PROCESS() {
        String fileName = JOptionPane.showInputDialog("Input file name");
        return new SIRQ(SWName.PROCESS_MANAGER, SIRQ.REQUEST_LOAD_PROCESS, fileName);
    }

}
