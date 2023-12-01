package visualizer.process_manager_panel;

import os.process_manager.ProcessManager;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTable;
import os.process_manager.Process;

import java.awt.*;
import java.util.Map;
import java.util.Set;

public class RunningProcessPanel extends MiniVMPanel {

    private ProcessManager processManager;

    private MiniVMTable PCB;
    private MiniVMTable pageTable;
    private MiniVMTable heap;

    public RunningProcessPanel(ProcessManager processManager) {
        this.processManager = processManager;
        setLayout(new GridLayout(3, 1));
        add(PCB = new MiniVMTable(new String[]{"Attribute", "Value"}));
        add(pageTable = new MiniVMTable(new String[]{"Page Number"}));
        add(heap = new MiniVMTable(new String[]{"Address", "Value"}));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        PCB.clear();
        Process runningProcess = processManager.getRunningProcess();
        PCB.add(new String[]{"ID", Integer.toString(runningProcess.id)});
        PCB.add(new String[]{"Name", runningProcess.name});
        PCB.add(new String[]{"CodeBase", Integer.toString(runningProcess.codeBase)});
        PCB.add(new String[]{"CodeSize", Integer.toString(runningProcess.codeSize)});
        PCB.add(new String[]{"DataBase", Integer.toString(runningProcess.dataBase)});
        PCB.add(new String[]{"DataSize", Integer.toString(runningProcess.dataSize)});
        PCB.add(new String[]{"StackBase", Integer.toString(runningProcess.stackBase)});
        PCB.add(new String[]{"StackSize", Integer.toString(runningProcess.stackSize)});
        PCB.add(new String[]{"HeapBase", Integer.toString(runningProcess.heapBase)});
        PCB.add(new String[]{"HeapSize", Integer.toString(runningProcess.heapSize)});
        pageTable.clear();
        for (Integer pageNumber : runningProcess.pageTable) pageTable.add(new String[]{Integer.toString(pageNumber)});
        heap.clear();
        Set<Integer> keySet = runningProcess.objectTable.keySet();
        for (Integer key : keySet)
            heap.add(new String[]{Integer.toString(key), Integer.toString(runningProcess.objectTable.get(key))});
    }

}
