package io.seventyfivef;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ZincV extends JFrame {
    JPanel mainPanel;
    JTextArea textInput;
    JLabel labelInput;
    JLabel labelFields;
    JRadioButton rbNone;
    JRadioButton rbInclude;
    JRadioButton rbExclude;
    ButtonGroup buttonGroup;
    JTextField textFields;
    JButton buttonParse;
    JButton buttonClear;
    JButton buttonCopy;
    JScrollPane scrollPane;
    JTable tableOutput;
    private final int leftMargin = 10;
    private final int rightMargin = 10;
    private final int topMargin = 0;
    private final int labelHeight = 20;
    private final int verticalSpacing = 5;

    private DefaultTableModel model = new DefaultTableModel();
    private String lastColumnSet;


    public void initializeDefaults() {
//        StringBuilder sb = new StringBuilder("ver: \"3.0\"\n");
//        int columns=10;
//        for (int i=0; i<columns; i++) {
//            sb.append(String.format("column %d",i+1));
//            if ( i<(columns-1)) sb.append(",");
//        }
//        sb.append("\n");
//        for (int r=0; r<2; r++) {
//            for (int i = 0; i < columns; i++) {
//                sb.append(String.format("data %d:%d", r, i + 1));
//                if ( i<(columns-1)) sb.append(",");
//            }
//            sb.append("\n");
//        }
//        textInput.setText(sb.toString());
    }

    public static <T> Object[] mergeArray(T[] arr1, T[] arr2)
    {
        return Stream.of(arr1, arr2).flatMap(Stream::of).toArray();
    }

    private boolean shouldIncludeColumn(String col) {
        boolean colInList = false;
        for (String colName: textFields.getText().split(",")) {
            if (colName.trim().equals(col)) {
                colInList = true;
                break;
            }
        }

        // TODO: This is crappy, do this better
        if ( rbNone.isSelected()) return true;
        if ( rbInclude.isSelected() ) {
            if ( colInList ) return true;
            return false;
        }

        if ( colInList ) return false;
        return true;

    }
    private void parseZinc() {
        int lineNbr = 0;
        String [] columnNames = {};
        model = new DefaultTableModel();
        ArrayList<Integer> dataLen = new ArrayList<>();
        ArrayList<Integer> skipIndexes = new ArrayList<>();
        ArrayList<Integer> includeIndexes = new ArrayList<>();
        ArrayList<Integer> modelIndexes = new ArrayList<>();
        model.addColumn("#");
        int dataLine = 0;
        int modelIndex = 0;
        for (String line: textInput.getText().split("\n")) {
            if ( lineNbr == 1) {
                lastColumnSet = line;
                System.out.println("Columns: "+line);
                int colIdx = 0;
                for(String col: line.split(",")) {
                    if ( shouldIncludeColumn(col)) {
                        System.out.println(String.format("Adding column %s (%d)", col, colIdx));
                        model.addColumn(col);
                        modelIndexes.add(colIdx);
                        modelIndex++;
                        dataLen.add(col.length());
                    } else {
                        System.out.println(String.format("Adding skip column %s (%d)", col, colIdx));
                        skipIndexes.add(colIdx);
                        modelIndexes.add(-1);
                        modelIndex++;
                        dataLen.add(0);
                    }
                    colIdx++;
                }
            } else if (lineNbr > 1) {
                ArrayList<String> dataRow = new ArrayList<>();
                dataRow.add(String.format("%04d", dataLine));
                dataLine++;

                int colIdx = 0;
                for(String dataElement : line.split(",")) {
                    if ( modelIndexes.get(colIdx) >= 0) {
                        dataRow.add(dataElement);
                        Integer len = dataElement.length();
                        if ( len > dataLen.get(colIdx)) {
                            dataLen.set(colIdx,len);
                        }
                    }
                    colIdx++;
                }

                model.addRow(dataRow.toArray());
            }
            lineNbr++;
        }
        tableOutput = new JTable(model);
        tableOutput.setOpaque(true);

        scrollPane.setViewportView(tableOutput);
        tableOutput.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i=0; i<modelIndexes.size();i++) {
            int idx = modelIndexes.get(i);
            if ( idx >= 0) {
                tableOutput.getColumnModel().getColumn(idx).setPreferredWidth(8 * dataLen.get(idx));
            }
        }
        positionControls();
    }

    public ZincV() {
        this.setLayout(null);
        this.setTitle("Zinc Viewer 0.1");
        this.setMinimumSize(new Dimension(1000, 600));
        this.setLocation(100,100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
//        mainPanel.setBackground(Color.BLUE);
        mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.black, Color.GRAY));

//        Insets insets = mainPanel.getInsets();
//        Dimension size;

        labelInput = new JLabel("Input data:");
        mainPanel.add(labelInput);

        textInput = new JTextArea();
        textInput.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(textInput);

        labelFields = new JLabel("Field Display:");
        mainPanel.add(labelFields);

        rbNone = new JRadioButton("No filtering");
        rbInclude = new JRadioButton("Include columns");
        rbExclude = new JRadioButton("Exclude columns");
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbNone);
        buttonGroup.add(rbInclude);
        buttonGroup.add(rbExclude);
        rbNone.setSelected(true);

        textFields = new JTextField();
        mainPanel.add(rbNone);
        mainPanel.add(rbInclude);
        mainPanel.add(rbExclude);
        mainPanel.add(textFields);

        buttonCopy = new JButton("<- Copy Columns");
        mainPanel.add(buttonCopy);
        buttonClear = new JButton("Clear");
        mainPanel.add(buttonClear);
        buttonParse = new JButton("Parse");
        mainPanel.add(buttonParse);

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textInput.setText("");
                textFields.setText("");
                tableOutput.setModel(new DefaultTableModel());
            }
        });

        buttonParse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseZinc();
            }
        });

        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                textFields.setText(lastColumnSet);
                rbInclude.setSelected(true);
            }
        });

        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane);

        this.pack();
        positionControls();

        this.setContentPane(mainPanel);

        initializeDefaults();
        this.setVisible(true);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println("Resized to " + e.getComponent().getSize());
                positionControls();
            }
            @Override
            public void componentMoved(ComponentEvent e) {
                System.out.println("Moved to " + e.getComponent().getLocation());
            }
        });
    }

    public void positionControls() {
        labelInput.setBounds(leftMargin, topMargin, 100, labelHeight);
        textInput.setBounds(leftMargin, topMargin + labelInput.getHeight()+verticalSpacing, this.getWidth()-leftMargin-rightMargin, 200);

        labelFields.setBounds(leftMargin, textInput.getY()+textInput.getHeight(), 100, labelHeight);
        rbNone.setBounds(leftMargin, labelFields.getY()+labelFields.getHeight(), 100, labelHeight);
        rbInclude.setBounds(leftMargin+150, labelFields.getY()+labelFields.getHeight(), 150, labelHeight);
        rbExclude.setBounds(leftMargin+300, labelFields.getY()+labelFields.getHeight(), 150, labelHeight);
        textFields.setBounds( leftMargin, rbNone.getY()+rbNone.getHeight(),800, 25);

        buttonCopy.setBounds(this.getWidth()-leftMargin-400, textInput.getY()+textInput.getHeight(), 150, 25);
        buttonClear.setBounds(this.getWidth()-leftMargin-200, textInput.getY()+textInput.getHeight(), 75, 25);
        buttonParse.setBounds(this.getWidth()-leftMargin-100, textInput.getY()+textInput.getHeight(), 75, 25);

        scrollPane.setBounds(leftMargin, textFields.getY()+textFields.getHeight()+verticalSpacing, this.getWidth()-leftMargin-rightMargin, this.getHeight()-50-(textFields.getY()+textFields.getHeight()+verticalSpacing));
        scrollPane.setBackground(Color.GREEN);

//        tableOutput.setLocation(0,0);
        if ( tableOutput != null ) {
            tableOutput.setBounds(0, 20, scrollPane.getWidth(), scrollPane.getHeight());
            tableOutput.setBackground(Color.YELLOW);
        }
    }

//    public static void main(String[] args) {
//        System.out.println("ZincViewer loading");
//        ZincV mainWindow = new ZincV();
//        System.out.println("ZincViewer unloading");
//    }
}
