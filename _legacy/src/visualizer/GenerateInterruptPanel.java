package visualizer;

import os.SIRQ;
import os.SWName;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class GenerateInterruptPanel extends JPanel {

    public GenerateInterruptPanel(SystemCall systemCall) {
        setPreferredSize(new Dimension(250, 680));
        add(new InterruptButton("REQUEST_LOAD_PROCESS", systemCall, this::REQUEST_LOAD_PROCESS));
        add(new InterruptButton("REQUEST_CHANGE_DIRECTORY", systemCall, this::REQUEST_CHANGE_DIRECTORY));
        add(new InterruptButton("REQUEST_KILL_PROCESS", systemCall, this::REQUEST_KILL_PROCESS));
    }

    private SIRQ REQUEST_CHANGE_DIRECTORY() {
        String dirName = JOptionPane.showInputDialog("Input directory name");
        return new SIRQ(SWName.FILE_MANAGER, SIRQ.REQUEST_CHANGE_DIRECTORY, dirName);
    }

    private SIRQ REQUEST_LOAD_PROCESS() {
        String fileName = JOptionPane.showInputDialog("Input file name");
        return new SIRQ(SWName.PROCESS_MANAGER, SIRQ.REQUEST_LOAD_PROCESS, fileName);
    }

    private SIRQ REQUEST_KILL_PROCESS() {
        return new SIRQ(SWName.PROCESS_MANAGER, SIRQ.REQUEST_KILL_PROCESS);
    }

}
