package visualizer.common;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MiniVMTextField extends JTextField {

    public MiniVMTextField(int width, int height, ActionListener al) {
        setPreferredSize(new Dimension(width, height));
        setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(5, 5, 5, 5)));
        setForeground(MiniVMColor.FONT_COLOR);
        setBackground(MiniVMColor.BACKGROUND);
        addActionListener(al);
        setCaretColor(MiniVMColor.FONT_COLOR);
        setFont(new MiniVMFont(15));
    }

}
