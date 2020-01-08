package guiComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import view.MainFrame;

public class TransparentListCellRenderer extends DefaultListCellRenderer {
	
	private final JLabel jlblCell = new JLabel(" ", SwingConstants.CENTER);
    Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
    Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);

    @Override
    public Component getListCellRendererComponent(JList jList, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        
        jlblCell.setOpaque(false);
        jlblCell.setText(value.toString());
        jlblCell.setFont(MainFrame.font.deriveFont(Font.PLAIN, 30));
        jlblCell.setForeground(Color.WHITE);
        
        if (isSelected) {
            jlblCell.setBorder(new LineBorder(Color.BLUE));
            jlblCell.setForeground(Color.BLACK);
        }

        jlblCell.setBorder(cellHasFocus ? lineBorder : emptyBorder);
        
        setForeground(Color.WHITE);
        setOpaque(isSelected);
        
        
        return jlblCell;
    }

}