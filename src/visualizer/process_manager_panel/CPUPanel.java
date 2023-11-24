package visualizer.process_manager_panel;

import hardware.cpu.CPUContext;
import os.process_manager.ProcessManager;
import visualizer.common.MiniVMPanel;

import java.awt.*;

public class CPUPanel extends MiniVMPanel {

    // model
    private ProcessManager processManager;

    public CPUPanel(ProcessManager processManager) {
        // set model
        this.processManager = processManager;
        // set attributes
        setLayout(new GridLayout(10, 4));
        // create components
        // update start
        updateStart();
    }

    @Override
    protected void updatePanel() {
        CPUContext cpuContext = processManager.getCPUContext();
    }


}
