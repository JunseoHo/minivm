package view;

import javax.swing.*;
import java.awt.*;

public class MemoryPanel extends TextAreaPanel {

    public MemoryPanel() {
        this.textArea.setEditable(false);
        this.textArea.setDisabledTextColor(Color.WHITE);
    }
}
