package gr.antoniosprovidakis.bestcoach.gui;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class LimitedPlainDocument extends javax.swing.text.PlainDocument {

    private int maxLen = -1;

    public LimitedPlainDocument() {
    }

    public LimitedPlainDocument(int maxLen) {
        this.maxLen = maxLen;
    }

    @Override
    public void insertString(int param, String str,
            javax.swing.text.AttributeSet attributeSet)
            throws javax.swing.text.BadLocationException {
        if (str != null && maxLen > 0 && getLength() + str.length() > maxLen) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }
        super.insertString(param, str, attributeSet);
    }
}
