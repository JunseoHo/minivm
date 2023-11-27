package os.compiler;

import java.util.LinkedList;
import java.util.List;

public class Compiler {

    public static List<Integer> compile(List<Byte> bytes) {
        StringBuilder program = new StringBuilder();
        for (Byte b : bytes) {
            if (b == 0) break;
            char c = (char) (b & 0xFF); // Convert byte to char
            program.append(c);
        }
        return compile(program.toString());
    }

    public static List<Integer> compile(String str) {
        String[] instructions = str.split("\n");
        List<Integer> machineCodes = new LinkedList<>();
        for (String instruction : instructions) {
            String[] tokens = instruction.split(" ");
            if (tokens.length != 5) return null;
            int addressingMode = toAddressingMode(tokens[0]);
            int opcode = toOpcode(tokens[1]);
            int register = toRegister(tokens[2]);
            int operand0 = toOperand(tokens[3]);
            int operand1 = toOperand(tokens[4]);
            if (addressingMode == -1 || opcode == -1 || register == -1 || operand0 == -1 || operand1 == -1) return null;
            int machineCode = 0;
            machineCode |= addressingMode;
            machineCode <<= 5;
            machineCode |= opcode;
            machineCode <<= 2;
            machineCode |= register;
            machineCode <<= 12;
            machineCode |= operand0;
            machineCode <<= 12;
            machineCode |= operand1;
            machineCodes.add(machineCode);
        }
        return machineCodes;
    }

    private static int toAddressingMode(String str) {
        int addressingMode = -1;
        switch (str) {
            case "im" -> addressingMode = 0;
            case "di" -> addressingMode = 1;
        }
        return addressingMode;
    }

    private static int toOpcode(String str) {
        int opcode = -1;
        switch (str) {
            case "hlt" -> opcode = 0;
            case "lda" -> opcode = 1;
            case "sto" -> opcode = 2;
            case "add" -> opcode = 3;
            case "sub" -> opcode = 4;
            case "mul" -> opcode = 5;
            case "div" -> opcode = 6;
            case "jmp" -> opcode = 7;
            case "jpz" -> opcode = 8;
            case "jpn" -> opcode = 9;
            case "int" -> opcode = 10;
            case "psh" -> opcode = 11;
            case "pop" -> opcode = 12;
            case "alc" -> opcode = 13;
            case "fre" -> opcode = 14;
        }
        return opcode;
    }

    private static int toRegister(String str) {
        int register = -1;
        switch (str) {
            case "AX" -> register = 0;
            case "BX" -> register = 1;
            case "CX" -> register = 2;
            case "DX" -> register = 3;
        }
        return register;
    }

    private static int toOperand(String str) {
        try {
            int operand = Integer.parseInt(str);
            if (operand > 4095 || operand < 0) return -1;
            return operand;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
