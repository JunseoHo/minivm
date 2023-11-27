package visualizer.process_manager_panel;

import hardware.cpu.CPUContext;
import os.process_manager.ProcessManager;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTable;

import java.awt.*;

public class CPUPanel extends MiniVMPanel {

    // model
    private ProcessManager processManager;
    private MiniVMTable cpuContext;

    public CPUPanel(ProcessManager processManager) {
        this.processManager = processManager;
        add(cpuContext = new MiniVMTable(new String[]{"Register", "Value"}), BorderLayout.CENTER);
        // update start
        updateStart();
    }

    @Override
    protected void updatePanel() {
        cpuContext.clear();
        CPUContext context = processManager.getCPUContext();
        cpuContext.add(new String[]{"PC", Integer.toString(context.PC)});
        cpuContext.add(new String[]{"MAR", Integer.toString(context.MAR)});
        cpuContext.add(new String[]{"MBR", Integer.toString(context.MBR)});
        cpuContext.add(new String[]{"IR_AM", Integer.toString(context.IR_AM)});
        cpuContext.add(new String[]{"IR_OPC", Integer.toString(context.IR_OPC)});
        cpuContext.add(new String[]{"IR_REG", Integer.toString(context.IR_REG)});
        cpuContext.add(new String[]{"IR_OPR0", Integer.toString(context.IR_OPR0)});
        cpuContext.add(new String[]{"IR_OPR1", Integer.toString(context.IR_OPR1)});
        cpuContext.add(new String[]{"ST", Integer.toString(context.ST)});
        cpuContext.add(new String[]{"INT", Integer.toString(context.INT)});
        cpuContext.add(new String[]{"CS", Integer.toString(context.CS)});
        cpuContext.add(new String[]{"DS", Integer.toString(context.DS)});
        cpuContext.add(new String[]{"SS", Integer.toString(context.SS)});
        cpuContext.add(new String[]{"SP", Integer.toString(context.SP)});
        cpuContext.add(new String[]{"HS", Integer.toString(context.HS)});
        cpuContext.add(new String[]{"HP", Integer.toString(context.HP)});
        cpuContext.add(new String[]{"AX", Integer.toString(context.AX)});
        cpuContext.add(new String[]{"BX", Integer.toString(context.BX)});
        cpuContext.add(new String[]{"CX", Integer.toString(context.CX)});
        cpuContext.add(new String[]{"DX", Integer.toString(context.DX)});
    }


}
