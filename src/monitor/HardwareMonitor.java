package monitor;

import common.Utils;
import hardware.Memory;
import hardware.cpu.CPU;
import hardware.cpu.Context;

import javax.swing.*;

public class HardwareMonitor extends JFrame {
    // hardware
    private CPU cpu;
    private Memory memory;
    // components
    private JTextArea cpuStatus;

    public HardwareMonitor(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
        setTitle("Hardware monitor");
        setSize(1280, 960);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(cpuStatus = new JTextArea());
        new Thread(new Updater()).start();
    }

    private class Updater implements Runnable {

        @Override
        public void run() {
            while (true) {
                Context context = cpu.save();
                String text = "PC                 : " + context.PC + "\n"
                        + "MAR                : " + context.MAR + "\n"
                        + "MBR                : " + context.MBR + "\n"
                        + "IR_ADDRESSING_MODE : " + context.IR_ADDRESSING_MODE + "\n"
                        + "IR_OPCODE          : " + context.IR_OPCODE + "\n"
                        + "IR_OPERAND_L       : " + context.IR_OPERAND_L + "\n"
                        + "IR_OPERAND_R       : " + context.IR_OPERAND_R + "\n"
                        + "TASKING            : " + context.tasking + "\n"
                        + "AC                 : " + context.AC + "\n"
                        + "CS                 : " + context.CS + "\n"
                        + "DS                 : " + context.DS + "\n";
                cpuStatus.setText(text);
                Utils.sleep(100);
            }
        }
    }

}
