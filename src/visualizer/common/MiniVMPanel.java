package visualizer.common;

import common.MiniVMUtils;

import javax.swing.*;
import java.awt.*;

public class MiniVMPanel extends JPanel {

    public MiniVMPanel() {
        this(new BorderLayout());
    }

    public MiniVMPanel(LayoutManager mgr) {
        setBackground(MiniVMColor.BACKGROUND);
        setLayout(mgr);
    }

    protected void updatePanel() {
        /* DO NOTHING */
    }

    protected void updateStart() {
        new Thread((new Updater())).start();
    }


    private class Updater implements Runnable {
        @Override
        public void run() {
            while (true) {
                updatePanel();
                MiniVMUtils.sleep(200);
            }
        }
    }

}
