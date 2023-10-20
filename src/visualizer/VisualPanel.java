package visualizer;

import hardware.cpu.CPU;
import hardware.cpu.Context;

import javax.swing.*;
import java.awt.*;

public class VisualPanel extends JPanel {

    private JTextArea textArea;
    private UpdateListener ul;

    public VisualPanel(UpdateListener ul) {
        this.ul = ul;
        textArea = new JTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setPreferredSize(new Dimension(400, 250));
        textArea.setEnabled(false);
        textArea.setEditable(false);
        textArea.setDisabledTextColor(Color.WHITE);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        add(textArea);
    }

    public void update() {
        textArea.setText(ul.update());
    }
}
