package visualizer;

import common.Utils;
import hardware.Memory;
import hardware.cpu.CPU;
import hardware.cpu.Context;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class MiniVMVisualizer extends JFrame {
    // model
    private CPU cpu;
    private Memory memory;
    private SystemCall systemCall;
    // components
    private VisualPanel cpuPanel = new VisualPanel(() -> cpuStatus());

    public MiniVMVisualizer(CPU cpu, Memory memory, SystemCall systemCall) {
        // set attributes
        this.cpu = cpu;
        this.memory = memory;
        this.systemCall = systemCall;
        setTitle("MiniVM");
        setSize(1280, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 3));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // add components
        add(cpuPanel);
        // start updater
        new Thread(new Updater()).start();
    }

    private void update() {
        cpuPanel.update();
    }

    private String cpuStatus() {
        Context context = cpu.save();
        return "[CPU]\n"
                + "PC                   : " + context.PC +"\n"
                + "MAR                  : " + context.MAR+"\n"
                + "MBR                  : " + context.MBR+"\n"
                + "IR_ADDRESSING_MODE   : " + context.IR_ADDRESSING_MODE+"\n"
                + "IR_OPCODE            : " + context.IR_OPCODE+"\n"
                + "IR_OPERAND_L         : " + context.IR_OPERAND_L+"\n"
                + "IR_OPERAND_R         : " + context.IR_OPERAND_R+"\n"
                + "TASKING              : " + context.tasking+"\n"
                + "AC                   : " + context.AC+"\n"
                + "CS                   : " + context.CS+"\n"
                + "DS                   : " + context.DS+"\n";
    }

    private class Updater implements Runnable {

        @Override
        public void run() {
            while (true) {
                update();
                Utils.sleep(100);
            }
        }
    }


}
