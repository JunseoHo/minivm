package visualizer.common;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MiniVMTextArea extends JTextArea {

    public MiniVMTextArea(int width, int height, boolean usable) {
        setPreferredSize(new Dimension(width, height));
        setEditable(usable);
        setEnabled(usable);
        setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        setDisabledTextColor(MiniVMColor.FONT_COLOR);
        setFont(new MiniVMFont(15));
        setBackground(MiniVMColor.AREA);
    }

}
