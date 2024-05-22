<h1 align="center">MiniVM</h1>
<div align="center"><i>Summary : This project is about implementation of hardware and operating system.<br>
The goal is to implement hardware working and resource management of operating system in Java</i></div>

<h1>Hardware</h1>
<h2>CPU</h2>
<h3>Instruction set architecture</h3>

Instruction size is **4byte**.<br>
Following is field table.

|    Field Name     |  Bit  | Description                                               |
|:-----------------:|:-----:|:----------------------------------------------------------|
|  Addressing mode  |   1   | Indicates whether the operand is a constant or an address |
|      Opcode       |   5   | Indicates Opcode                                          |
|     Register      |   2   | Indicates general purpose register (AX, BX, CX, DX)       |
|     Operand 0     |  12   | Indicates first operand                                   |
|     Operand 1     |  12   | Indicates second operand                                  |

And following is Opcode table.<br>
Segment designation is done through Operand 0, with 0 being considered the data segment, 1 being the stack segment, and
2 being the heap segment.

|     Name      | Mnemonic | Description                                 |                                                  
|:-------------:|:--------:|:--------------------------------------------|
|     Halt      |   HLT    | Create halt interrupt                       |                                             
|     Load      |   LDA    | Load value into AX                          |                                               
|     Store     |   STO    | Store AX into memory                        | 
|      Add      |   ADD    | Add value into AX and store AX              |                                                  
|   Subtract    |   SUB    | Subtract value from AX and store AX         |                                                  
|   Multiply    |   MUL    | Multiply value with AX and store AX         |                                                  
|    Divide     |   DIV    | Divide value with AX and store AX           |                                                  
|     Jump      |   JMP    | Store value into PC                         |                                                  
|   Jump zero   |   JPZ    | If zero field is true, execute Jump         |                                                  
| Jump negative |   JPN    | If negative field is true, execute Jump     |                                                  
|   Interrupt   |   INT    | Invert interrupt field                      |                                                  
|     Push      |   PSH    | Push value into stack and increase SP       |                                                  
|      Pop      |   POP    | decrease SP                                 |                                                  
|   Allocate    |   ALC    | Allocate heap memory and store base into AX |                                                  
|     Free      |   FRE    | Free heap memory                            |                                                  

# Oprating System

## File System

![file_system.png](images%2Ffile_system.png)

- The left panel is a memory dump of the FAT.
- The center panel is a memory dump of the data section.
- The right panel is a panel where you can open, update, and close the file.

The Memory Dump panel automatically shows you where physical values in the disk have changed when they do.

## Memory Manager

![memory_manager.png](images%2Fmemory_manager.png)

- The status bar at the top is the percentage of memory currently in use.
- The top panel is the I/O log for main memory.
- The bottom panel is the alloc/free log for main memory.

## Process Manager

![procrss_manager.png](images%2Fprocrss_manager.png)

- The left panel is the CPU's register values.
- The center panel is the running process's PCB, allocated pages, and allocated heap memory.
- The right panel is a list of processes loaded on main memory.