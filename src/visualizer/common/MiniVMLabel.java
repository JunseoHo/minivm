package visualizer.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MiniVMLabel extends JLabel {

    public MiniVMLabel(String text) {
        super(text);
        setFont(new MiniVMFont(20));
        setForeground(MiniVMColor.FONT_COLOR);
        setBackground(Color.BLUE);
        setBorder(new EmptyBorder(20, 20, 10, 10));
    }

}
