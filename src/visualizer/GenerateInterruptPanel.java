package visualizer;

import os.SIQ;
import os.SWName;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class GenerateInterruptPanel extends JPanel {

    public GenerateInterruptPanel(SystemCall systemCall) {
        // set attributes
        setPreferredSize(new Dimension(250, 680));
        add(new InterruptButton("REQUEST_LOAD_PROCESS", systemCall, () -> REQUEST_LOAD_PROCESS()));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
        add(new JButton("REQUEST_LOAD_PROCESS"));
    }

    private SIQ REQUEST_LOAD_PROCESS() {
        return new SIQ(SWName.PROCESS_MANAGER, SIQ.REQUEST_LOAD_PROCESS, "Sum");
    }

}
