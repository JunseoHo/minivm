package hardware.cpu;

public record Context(long PC, long MAR, long MBR, long IR_ADDRESSING_MODE, long IR_OPCODE, long IR_OPERAND_L,
                      long IR_OPERAND_R, long AC, long CS, long DS, boolean isRunning, boolean isZero, boolean halt,
                      boolean waitIO, boolean timeSliceExpired) {
}
