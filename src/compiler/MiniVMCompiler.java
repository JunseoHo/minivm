package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MiniVMCompiler {

    private static final String PROGRAM_PATH = "./assembly_source_sample/";

    public static void main(String[] args) throws FileNotFoundException {
        File file = null;
        // import file
        while (file == null) {
            System.out.print("Input file name : ");
            String fileName = new Scanner(System.in).nextLine().trim();
            file = new File(PROGRAM_PATH + fileName);
            if (file.exists()) break;
            else {
                System.out.println("File not found.");
                file = null;
            }
        }
        // compile
        List<String> machineCodes = new LinkedList<>();
        Scanner tokenizer = new Scanner(file);
        while (tokenizer.hasNextLine()) {
            String line = tokenizer.nextLine();
            String[] inst = line.trim().split(" ");
            String machineCode = "";
            String addressingMode = inst[0];
            String opcode = inst[1];
            // addressing mode
            if (addressingMode.equalsIgnoreCase("im")) machineCode += "00";
            else if (addressingMode.equalsIgnoreCase("di")) machineCode += "01";
            else if (addressingMode.equalsIgnoreCase("id")) machineCode += "10";
            else {
                System.err.println("Parse Error.\nUnknown addressing mode : " + line);
                return;
            }
            // opcode
            if (opcode.equalsIgnoreCase("hlt")) machineCode += "0000";
            else if (opcode.equalsIgnoreCase("lda")) machineCode += "0001";
            else if (opcode.equalsIgnoreCase("sto")) machineCode += "0010";
            else if (opcode.equalsIgnoreCase("add")) machineCode += "0011";
            else if (opcode.equalsIgnoreCase("sub")) machineCode += "0100";
            else if (opcode.equalsIgnoreCase("mul")) machineCode += "0101";
            else if (opcode.equalsIgnoreCase("jmp")) machineCode += "0110";
            else if (opcode.equalsIgnoreCase("jpz")) machineCode += "0111";
            else if (opcode.equalsIgnoreCase("rdm")) machineCode += "1000";
            else if (opcode.equalsIgnoreCase("wrm")) machineCode += "1001";
            else if (opcode.equalsIgnoreCase("int")) machineCode += "1010";
            else {
                System.err.println("Parse Error.\nUnknown opcode : " + line);
                return;
            }
            // operand
            if (opcode.equalsIgnoreCase("wrm")) {
                if (inst.length != 5) {
                    System.out.println("Parse Error.\nInvalid operand numbers : " + line);
                    return;
                }
                String operand0 = parseOperand(inst[2], 3);
                String operand1 = parseOperand(inst[3], 10);
                String operand2 = parseOperand(inst[4], 13);
                if (operand0 == null || operand1 == null || operand2 == null) {
                    System.err.println("Parse Error.\nInvalid operand : " + line);
                    return;
                }
                machineCode += operand0 + operand1 + operand2;
            } else {
                if (inst.length != 4) {
                    System.out.println("Parse Error.\nInvalid operand numbers : " + line);
                    return;
                }
                String operand0 = parseOperand(inst[2], 13);
                String operand1 = parseOperand(inst[3], 13);
                if (operand0 == null || operand1 == null) {
                    System.err.println("Parse Error.\nInvalid operand : " + line);
                    return;
                }
                machineCode += operand0 + operand1;
            }
            // add machine code
            machineCodes.add(machineCode);
        }
        // print
        for (String inst : machineCodes) System.out.println(inst);
        System.out.println("\nSize : " + machineCodes.size());
    }

    public static String parseOperand(String operand, int limit) {
        try {
            long num = Long.parseLong(operand);
            StringBuilder bin = new StringBuilder(Long.toBinaryString(num));
            while (bin.length() < limit) bin.insert(0, '0');
            if (bin.length() > limit) bin = new StringBuilder(bin.substring(bin.length() - limit));
            return bin.toString();
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
