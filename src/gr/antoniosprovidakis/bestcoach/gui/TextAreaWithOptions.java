package gr.antoniosprovidakis.bestcoach.gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class TextAreaWithOptions extends JTextArea {

    private RightClickMenu menu;

    public TextAreaWithOptions() {
        super();
        menu = new RightClickMenu(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getComponent() instanceof JTextArea) {
                    RightClickMenu popup = menu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            // this runs on Mac OS X

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && e.getComponent() instanceof JTextArea) {
                    RightClickMenu popup = menu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public class RightClickMenu extends JPopupMenu {

        private JMenuItem mICopy;
        private JMenuItem mICut;
        private JMenuItem mIPaste;
        private JMenuItem mISelectAll;
        private JTextArea textArea;

        public RightClickMenu(JTextArea tArea) {
            super();
            textArea = tArea;

            mICopy = new JMenuItem(new AbstractAction("Copy") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.copy();
                }
            });

            mICut = new JMenuItem(new AbstractAction("Cut") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.cut();
                }
            });

            mIPaste = new JMenuItem(new AbstractAction("Paste") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.paste();
                }
            });

            mISelectAll = new JMenuItem(new AbstractAction("Select All") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.selectAll();
                }
            });

            add(mICopy);
            add(mICut);
            add(mIPaste);
            add(new JSeparator());
            add(mISelectAll);
        }
    }
}
