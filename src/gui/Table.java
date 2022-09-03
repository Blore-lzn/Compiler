package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class Table {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    
    public Table(String name) {
        initGUI(name);
    }
    
    private void initGUI(String name) {
        mainFrame = new JFrame(name);
        mainFrame.setSize(500, 400);
        mainFrame.setLayout(new GridLayout(3, 1));
        
        headerLabel = new JLabel("", JLabel.CENTER);
        JLabel statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setSize(350, 100);
        
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }
    
    public void show(String title, Object[][] data, String[] columnNames) {
        headerLabel.setText(title);
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize(300, 300);
        table.setFillsViewportHeight(true);
        controlPanel.add(scrollPane);
        mainFrame.setVisible(true);
    }
}


