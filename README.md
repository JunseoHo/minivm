<h1 align="center">MiniVM</h1>
<div align="center"><i>Summary : This project is about implementation of hardware and operating system.<br>
The goal is to implement hardware working and resource management of operating system in Java</i></div>
<h1>Contents</h1>
<ol type='I'>
<li>Hardware
    <ol>
        <li>CPU
            <ol>
                <li>Register</li>
                <li>Instruction set architecture</li>
            </ol>
        </li>
        <li>MMU</li>
            <ol>
                <li>Address translation</li>
            </ol>
        <li>RAM
            <ol>
                <li>Frame</li>
            </ol>
        </li>
        <li>Disk
            <ol>
                <li>Sector</li>
                <li>Flatter</li>
            </ol>
        </li>
    </ol>
<li>Operating system
    <ol>
        <li>Process manager
            <ol>
                <li>Thread</li>
                <li>Process</li>
                <li>Scheduler</li>
            </ol>
        </li>
        <li>Memory manager
             <ol>
                <li>Page</li>
                <li>Heap</li>
            </ol>
        </li>
        <li>File manager
            <ol>
                <li>Cluster</li>
                <li>File allocation table</li>
                <li>Directory entry</li>
                <li>Executable</li>
            </ol>
        </li> 
    </ol>
</li>
</ol>

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




