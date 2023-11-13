package visualizer.common;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MiniVMButton extends JButton {

    public MiniVMButton(String text, ActionListener al) {
        super(text);
        addActionListener(al);
    }

}
