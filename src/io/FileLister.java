package io;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;

public class FileLister extends Frame implements ActionListener, ItemListener {
    private List list;
    private TextField details;
    private Panel buttons;
    private Button up, close;
    private File currentDir;
    private FilenameFilter filter;
    private String[] files;
    private DateFormat dateFormatter =
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public FileLister(String directory, FilenameFilter filter) {
        super("File Lister");
        this.filter = filter;

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        list = new List(12, false);
        list.setFont(new Font("MonoSpaced", Font.PLAIN, 14));
        list.addActionListener(this);
        list.addItemListener(this);

        details = new TextField();
        details.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
        details.setEditable(false);

        buttons = new Panel();
        buttons.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttons.setFont(new Font("SansSerif", Font.BOLD, 14));

        up = new Button("Up a Directory");
        up.addActionListener(this);

        close = new Button("Close");
        close.addActionListener(this);

        buttons.add(up);
        buttons.add(close);

        this.add(list, "Center");
        this.add(details, "North");
        this.add(buttons, "South");
        this.setSize(500, 350);

        listDirectory(directory);
    }

    public void listDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.isDirectory())
            throw new IllegalArgumentException("FileLister: no such directory");

        files = dir.list(filter);

        java.util.Arrays.sort(files);

        list.removeAll();
        list.add("[Up to Parent Directory]");
        for (int i = 0; i < files.length; i++) list.add(files[i]);

        this.setTitle(directory);
        details.setText(directory);

        currentDir = dir;
    }
}
