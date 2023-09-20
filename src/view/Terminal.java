package view;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Terminal extends TextAreaPanel {


    public Terminal() {
        this.appendString("Welcome to Mini-OS\n");
        this.appendString("Project for System programming2 Myongji Univ. 2023-2 \n");
        this.appendString("$> ");
    }

}
