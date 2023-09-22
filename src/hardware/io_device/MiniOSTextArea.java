package hardware.io_device;

import javax.swing.*;
import java.awt.*;

public class MiniOSTextArea extends JScrollPane {

    protected JTextArea textArea;

    public MiniOSTextArea() {
        setViewportView(textArea = new JTextArea());
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setDisabledTextColor(Color.WHITE);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        textArea.setEditable(false);
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    protected void append(String text) {
        textArea.append("\n" + text);
    }

}
