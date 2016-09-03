package vkmessenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import messages.Message;
import messages.MessageUtils;
import user.User;

/**
 *
 * @author Игорь
 */
public class VKMessenger {

    private JFrame mainFrame;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JPanel buttonPanel;
    private JPanel friendsPanel;
    private JPanel mainPanel;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane;
    private JTextArea outputText;
    private JTextField inputText;
    private JButton button;
    private JComboBox allFriends;
    private LookAndFeelInfo[] view;
    private DefaultComboBoxModel boxModel;
    private ApplicationController ac;
    private User currentUser;
    private String own;
    private JTextArea currentText;
    private HashMap<User, JTextArea> hashMap;
    private int sms;

    public VKMessenger(ApplicationController ac) {
        view = UIManager.getInstalledLookAndFeels();
        try {
            UIManager.setLookAndFeel(view[1].getClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        this.ac = ac;
        this.boxModel = ac.getBoxModel();
        initComponents();
        currentUser = ac.getList().get(0);
        currentText = outputText;
        hashMap = new HashMap<>();
        hashMap.put(currentUser, currentText);
        own = ac.getOwn().toString();
        try {
            sms = MessageUtils.getLastMessage(ac.getAccess());
        } catch (Exception ex) {
            Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    int temp = 0;

                    try {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        temp = MessageUtils.getLastMessage(ac.getAccess());
                    } catch (Exception ex) {
                        Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    List<Message> list = null;

                    if (temp > sms) {
                        int res = temp - sms;
                        sms = temp;
                        try {
                            list = MessageUtils.getLastMessages(ac.getAccess(), res);
                        } catch (Exception ex) {
                            Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        continue;
                    }

                    for (Message a : list) {
                        String str = a.getFrom_id();
                        User u = null;
                        try {
                            u = User.getUserById(str);
                            System.out.println(u);
                        } catch (IOException ex) {
                            Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (hashMap.containsKey(u)) {
                            try {
                                hashMap.get(u).append(User.getUserById(a.getFrom_id().toString()) + ": " + a.getBody() + '\n');
                            } catch (IOException ex) {
                                Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            hashMap.put(u, new JTextArea());
                            try {
                                hashMap.get(u).append(User.getUserById(a.getFrom_id().toString()) + ": " + a.getBody() + '\n');
                            } catch (IOException ex) {
                                Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
        t.start();;
    }

    public void showGUI() {
        mainFrame.setVisible(true);
    }

    private void initLabels() {
        jLabel1 = new JLabel("Получено:");
        jLabel2 = new JLabel("Ответ:");
    }

    private void initInputElement() {
        inputText = new JTextField(35);
    }

    private void initOutputElement() {
        outputText = new JTextArea(10, 35);
        outputText.setEditable(false);
        outputText.setLineWrap(true);
    }

    private void initButton() {
        button = new JButton("Отправить");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = inputText.getText().trim();
                String str1 = str;
                //str = str.replace(" ", "%20");
                try {
                    MessageUtils.sendMessage(str, currentUser.getId(), ac.getAccess());
                } catch (IOException ex) {
                    Logger.getLogger(VKMessenger.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                outputText.append(own + ": " + str1 + "\n");
                inputText.setText("");
            }
        });
    }

    private void initButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        buttonPanel.add(button);
    }

    private void initScrollPane() {
        jScrollPane = new JScrollPane();
        outputText.setBackground(Color.WHITE);
        outputText.setOpaque(true);
        jScrollPane.setViewportView(outputText);
        outputText.setVisible(true);
    }

    private void initInputPanel() {
        inputPanel = new JPanel(new BorderLayout(0, 5));
        inputPanel.add(BorderLayout.NORTH, jLabel2);
        inputPanel.add(BorderLayout.CENTER, inputText);
        inputPanel.add(BorderLayout.SOUTH, buttonPanel);
    }

    private void initOutputPanel() {
        outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(BorderLayout.NORTH, jLabel1);
        outputPanel.add(BorderLayout.CENTER, jScrollPane);
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(BorderLayout.CENTER, outputPanel);
        mainPanel.add(BorderLayout.SOUTH, inputPanel);
    }

    private void initAllFriendsComboBox() {
        allFriends = new JComboBox();
        allFriends.setMaximumRowCount(10);
        allFriends.setModel(boxModel);
        allFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentUser = ac.getList().get(allFriends.getSelectedIndex());
                if (hashMap.containsKey(currentUser)) {
                    outputText.setVisible(false);
                    currentText = hashMap.get(currentUser);
                    outputText = currentText;
                    jScrollPane.setViewportView(outputText);
                    outputText.setEditable(false);
                    outputText.setLineWrap(true);
                    outputText.setVisible(true);
                } else {
                    JTextArea curr = new JTextArea();
                    hashMap.put(currentUser, curr);
                    outputText.setVisible(false);
                    currentText = hashMap.get(currentUser);
                    outputText = currentText;
                    jScrollPane.setViewportView(outputText);
                    outputText.setEditable(false);
                    outputText.setLineWrap(true);
                    outputText.setVisible(true);
                }
            }
        });
    }

    private void initFriendsPanel() {
        friendsPanel = new JPanel(new GridLayout(1, 4, 3, 10));
        friendsPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 3));
        friendsPanel.add(new JLabel(""));
        friendsPanel.add(allFriends);
    }

    private void initFrame() {
        mainFrame = new JFrame("Сообщения");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.add(BorderLayout.NORTH, friendsPanel);
        mainFrame.add(BorderLayout.CENTER, mainPanel);
        mainFrame.pack();
        centered(mainFrame);
    }

    private void centered(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    private void initComponents() {
        initLabels();
        initButton();
        initInputElement();
        initOutputElement();
        initScrollPane();
        initButtonPanel();
        initInputPanel();
        initOutputPanel();
        initMainPanel();
        initAllFriendsComboBox();
        initFriendsPanel();
        initFrame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new VKMessenger().showGUI();    
            }
        });
    }

}
