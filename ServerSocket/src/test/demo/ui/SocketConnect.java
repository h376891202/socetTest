package test.demo.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class SocketConnect implements ActionListener{
    JFrame frame;
    JButton connect;
    JTextField ipInput;
    JButton exit;

    public SocketConnect() {
        frame = new JFrame("链接");
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);
        Container contentPane = frame.getContentPane();

        GridBagConstraints c = new GridBagConstraints();

        connect = new JButton("链接");
        connect.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 10, 20);
        c.gridx = 0;
        c.gridy = 1;
        contentPane.add(connect, c);

        exit = new JButton("退出");
        exit.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 10, 20);
        c.gridx = 1;
        c.gridy = 1;
        contentPane.add(exit, c);

        ipInput = new JTextField(15);
        ipInput.setPreferredSize(new Dimension(150, 30));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 0, 20);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        contentPane.add(ipInput, c);

        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource().equals(exit)) {
            System.exit(0);
        }
    }

    public static void main(String args []) {
        new SocketConnect();
    }

}