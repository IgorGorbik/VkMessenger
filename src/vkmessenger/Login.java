package vkmessenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import token.VKToken;

/**
 *
 * @author Игорь
 */
public class Login {

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel enterPanel;
    private JPanel buttonPanel;
    private JPanel welcomePanel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JTextField loginText;
    private JPasswordField passwordText;
    private JButton button;
    private UIManager.LookAndFeelInfo[] view;
    private String access_token = null;

    private void initLoginField() {
        loginText = new JTextField(9);
    }

    private void initPasswordField() {
        passwordText = new JPasswordField(9);
    }

    private void initLabels() {
        label1 = new JLabel("Login");
        label2 = new JLabel("Password");
        label3 = new JLabel("Welcome to VK");
        label3.setForeground(Color.BLUE);
        label4 = new JLabel("");
        label4.setForeground(Color.RED);
    }

    private void initButton() {
        button = new JButton("Enter");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (access_token == null) {
                    try {
                        access_token = VKToken.getToken("5251743", "messages,friends",
                                loginText.getText().trim(), passwordText.getText().trim());
                        System.out.println(access_token);
                    } catch (SocketTimeoutException exc) {
                        label4.setText("Connection problem         ");
                        exc.printStackTrace();
                        return;
                    } catch (Exception exc) {
                        label4.setText("Invalid password or login         ");
                        exc.printStackTrace();
                        return;
                    }
                }

                ApplicationController ac = null;
                try {
                    ac = new ApplicationController(access_token);
                } catch (Exception ex) {
                    label4.setText("Connection problem         ");
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                new VKMessenger(ac).showGUI();;
                frame.dispose();
            }
        });
    }

    private void initEnterPanel() {
        enterPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        enterPanel.add(label1);
        enterPanel.add(loginText);
        enterPanel.add(label2);
        enterPanel.add(passwordText);
    }

    private void initButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(label4);
        buttonPanel.add(button);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
    }

    private void initWelcomePanel() {
        welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        welcomePanel.add(label3);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
    }

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(BorderLayout.NORTH, welcomePanel);
        mainPanel.add(BorderLayout.CENTER, enterPanel);
        mainPanel.add(BorderLayout.SOUTH, buttonPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 25, 30));
    }

    private void initFrame() {
        frame = new JFrame("VKMessenger");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(mainPanel);
        frame.pack();
        centered(frame);
    }

    private void initComponents() {
        initLoginField();
        initPasswordField();
        initButton();
        initLabels();
        initEnterPanel();
        initButtonPanel();
        initWelcomePanel();
        initMainPanel();
        initFrame();
    }

    private void centered(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public void showEnterGUI() {
        frame.setVisible(true);
    }

    public Login() {
        view = UIManager.getInstalledLookAndFeels();
        try {
            UIManager.setLookAndFeel(view[1].getClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().showEnterGUI();
            }
        });
    }

}
