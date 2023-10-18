<h1 align="center">minivm</h1>
<div align="center"><i>Summary : This project is about implementation of hardware and operating system.<br>The goal is to implement a simple virtual machine in Java.</i></div>
<h1>Contents</h1>
<ol type='I'>
  <li>Instruction set</li>
  <li>General hardware interrupt</li>
  <li>Module structure</li>
</ol>

<h1>Instruction set</h1>

<h1>General hardware interrupt</h1>
Following interrupts are shared by all hardware.

|   ID   |  Mnemonic  | Name                                           | Description                |
|:------:|:----------:|:-----------------------------------------------|:---------------------------|
|  0x00  |    SHC     | **S**ignal **H**ealth **C**heck                | Check status of hardware.  |
|  0x01  |    AHC     | **A**cknowledge **H**ealth **C**heck           | Positive response for SHC  |
|  0x02  |    NHC     | **N**egative Acknowledge **H**ealth **C**heck  | Negative response for SHC  |
