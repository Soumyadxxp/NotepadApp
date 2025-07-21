import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;

public class Notepad extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private JMenuItem newItem, openItem, saveItem, findItem, replaceItem, wordWrapItem, fontItem, printItem, exitTabItem, darkModeItem;
    private boolean wordWrap = false;
    private boolean darkMode = false;

    public Notepad() {
        setTitle("Java Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu formatMenu = new JMenu("Format");
        JMenu viewMenu = new JMenu("View");

        newItem = new JMenuItem("New", new ImageIcon("icons/new.png")); // Add icons for a professional look
        openItem = new JMenuItem("Open", new ImageIcon("icons/open.png"));
        saveItem = new JMenuItem("Save", new ImageIcon("icons/save.png"));
        printItem = new JMenuItem("Print", new ImageIcon("icons/print.png"));
        exitTabItem = new JMenuItem("Exit Tab", new ImageIcon("icons/exit.png"));
        findItem = new JMenuItem("Find", new ImageIcon("icons/find.png"));
        replaceItem = new JMenuItem("Replace", new ImageIcon("icons/replace.png"));
        wordWrapItem = new JMenuItem("Toggle Word Wrap", new ImageIcon("icons/wordwrap.png"));
        fontItem = new JMenuItem("Font", new ImageIcon("icons/font.png"));
        darkModeItem = new JMenuItem("Toggle Dark Mode", new ImageIcon("icons/darkmode.png"));

        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        printItem.addActionListener(this);
        exitTabItem.addActionListener(this);
        findItem.addActionListener(this);
        replaceItem.addActionListener(this);
        wordWrapItem.addActionListener(this);
        fontItem.addActionListener(this);
        darkModeItem.addActionListener(this);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(printItem);
        fileMenu.add(exitTabItem);
        editMenu.add(findItem);
        editMenu.add(replaceItem);
        formatMenu.add(wordWrapItem);
        formatMenu.add(fontItem);
        viewMenu.add(darkModeItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        applyLightMode(); // Start with light mode by default
        newTab();
    }

    private void newTab() {
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(wordWrap);
        textArea.setWrapStyleWord(wordWrap);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        tabbedPane.addTab("Untitled", new JScrollPane(textArea));
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        applyTheme(textArea); // Apply the current theme to the new tab
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JTextArea textArea = new JTextArea();
                textArea.read(reader, null);
                tabbedPane.addTab(file.getName(), new JScrollPane(textArea));
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                applyTheme(textArea); // Apply the current theme to the opened file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All Files", "*"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (fileChooser.getFileFilter().getDescription().equals("Text Files") && !file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
                textArea.write(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findText() {
        String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
        if (searchText != null) {
            JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
            String content = textArea.getText();
            int index = content.indexOf(searchText);
            if (index != -1) {
                textArea.setCaretPosition(index);
                textArea.select(index, index + searchText.length());
                textArea.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Text not found.");
            }
        }
    }

    private void replaceText() {
        JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
        String findText = JOptionPane.showInputDialog(this, "Find:");
        String replaceText = JOptionPane.showInputDialog(this, "Replace with:");
        if (findText != null && replaceText != null) {
            textArea.setText(textArea.getText().replace(findText, replaceText));
        }
    }

    private void toggleWordWrap() {
        wordWrap = !wordWrap;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getComponentAt(i)).getViewport().getView();
            textArea.setLineWrap(wordWrap);
            textArea.setWrapStyleWord(wordWrap);
        }
    }

    private void changeFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(this, "Select Font:", "Font",
                JOptionPane.PLAIN_MESSAGE, null, fonts, fonts[0]);

        if (selectedFont != null) {
            String sizeStr = JOptionPane.showInputDialog(this, "Enter Font Size:", "14");
            try {
                int size = Integer.parseInt(sizeStr);
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getComponentAt(i)).getViewport().getView();
                    textArea.setFont(new Font(selectedFont, Font.PLAIN, size));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid font size.");
            }
        }
    }

    private void printFile() {
        try {
            JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
            boolean printed = textArea.print();
            if (printed) JOptionPane.showMessageDialog(this, "Print successful.");
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void exitTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            JTextArea textArea = (JTextArea) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            if (textArea.getText().length() > 0) {
                int option = JOptionPane.showConfirmDialog(this, "Do you want to save changes before closing?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                } else if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            tabbedPane.remove(selectedIndex);
        }
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        if (darkMode) {
            applyDarkMode();
        } else {
            applyLightMode();
        }
    }

    private void applyDarkMode() {
        // Set dark theme colors
        Color darkBackground = new Color(30, 30, 30);
        Color darkForeground = new Color(200, 200, 200);
        Color darkMenuBackground = new Color(50, 50, 50);

        // Apply to all components
        getContentPane().setBackground(darkBackground);
        tabbedPane.setBackground(darkBackground);
        tabbedPane.setForeground(darkForeground);

        for (Component component : getComponents()) {
            applyTheme(component, darkBackground, darkForeground);
        }

        // Apply to menu bar and menus
        JMenuBar menuBar = getJMenuBar();
        menuBar.setBackground(darkMenuBackground);
        menuBar.setForeground(darkForeground);

        for (Component menu : menuBar.getComponents()) {
            if (menu instanceof JMenu) {
                ((JMenu) menu).setBackground(darkMenuBackground);
                ((JMenu) menu).setForeground(darkForeground);
                for (Component item : ((JMenu) menu).getMenuComponents()) {
                    if (item instanceof JMenuItem) {
                        ((JMenuItem) item).setBackground(darkMenuBackground);
                        ((JMenuItem) item).setForeground(darkForeground);
                    }
                }
            }
        }

        // Apply to text areas in tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component component = tabbedPane.getComponentAt(i);
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) view;
                    textArea.setBackground(darkBackground);
                    textArea.setForeground(darkForeground);
                    textArea.setCaretColor(darkForeground);
                }
            }
        }
    }

    private void applyLightMode() {
        // Set light theme colors
        Color lightBackground = Color.WHITE;
        Color lightForeground = Color.BLACK;
        Color lightMenuBackground = new Color(240, 240, 240);

        // Apply to all components
        getContentPane().setBackground(lightBackground);
        tabbedPane.setBackground(lightBackground);
        tabbedPane.setForeground(lightForeground);

        for (Component component : getComponents()) {
            applyTheme(component, lightBackground, lightForeground);
        }

        // Apply to menu bar and menus
        JMenuBar menuBar = getJMenuBar();
        menuBar.setBackground(lightMenuBackground);
        menuBar.setForeground(lightForeground);

        for (Component menu : menuBar.getComponents()) {
            if (menu instanceof JMenu) {
                ((JMenu) menu).setBackground(lightMenuBackground);
                ((JMenu) menu).setForeground(lightForeground);
                for (Component item : ((JMenu) menu).getMenuComponents()) {
                    if (item instanceof JMenuItem) {
                        ((JMenuItem) item).setBackground(lightMenuBackground);
                        ((JMenuItem) item).setForeground(lightForeground);
                    }
                }
            }
        }

        // Apply to text areas in tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component component = tabbedPane.getComponentAt(i);
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) view;
                    textArea.setBackground(lightBackground);
                    textArea.setForeground(lightForeground);
                    textArea.setCaretColor(lightForeground);
                }
            }
        }
    }

    private void applyTheme(Component component, Color background, Color foreground) {
        component.setBackground(background);
        component.setForeground(foreground);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyTheme(child, background, foreground);
            }
        }
    }

    private void applyTheme(JTextArea textArea) {
        if (darkMode) {
            textArea.setBackground(new Color(30, 30, 30));
            textArea.setForeground(new Color(200, 200, 200));
            textArea.setCaretColor(new Color(200, 200, 200));
        } else {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            textArea.setCaretColor(Color.BLACK);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newItem) newTab();
        else if (e.getSource() == openItem) openFile();
        else if (e.getSource() == saveItem) saveFile();
        else if (e.getSource() == findItem) findText();
        else if (e.getSource() == replaceItem) replaceText();
        else if (e.getSource() == wordWrapItem) toggleWordWrap();
        else if (e.getSource() == fontItem) changeFont();
        else if (e.getSource() == printItem) printFile();
        else if (e.getSource() == exitTabItem) exitTab();
        else if (e.getSource() == darkModeItem) toggleDarkMode();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Notepad().setVisible(true));
    }
}