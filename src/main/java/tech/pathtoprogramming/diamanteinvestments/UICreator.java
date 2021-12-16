package tech.pathtoprogramming.diamanteinvestments;

import tech.pathtoprogramming.diamanteinvestments.model.Bounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UICreator {

    private UICreator() {
    }

    public static JTextField createTextField(String id, Bounds bounds) {
        JTextField textField = new JTextField();
        textField.setName(id);
        textField.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        textField.setColumns(10);
        return textField;
    }

    public static JPasswordField createPasswordField(String id, Bounds bounds) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setName(id);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        passwordField.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return passwordField;
    }

    public static JLabel createLabel(String id, String text, Font font, Bounds bounds) {
        JLabel label = new JLabel(text);
        label.setName(id);
        label.setFont(font);
        label.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return label;
    }

    public static JButton createButton(String id, String text, Bounds bounds, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setName(id);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
        button.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        button.addActionListener(actionListener);
        return button;
    }

    public static void addAllToContentPane(Container container, JComponent... component) {
        for (JComponent c : component) {
            container.add(c);
        }
    }
}
