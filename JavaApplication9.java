/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication9;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import net.ucanaccess.jdbc.UcanaccessDriver;

public class JavaApplication9 extends JFrame {
    private JTextField bookIDField, titleField, authorField, yearField;
    private JButton addButton, deleteButton, refreshButton;
    private JTable bookTable;
    DefaultTableModel tableModel;

    private Connection connection;

    public JavaApplication9() {
        setTitle("Library System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the database connection
        initializeDB();

        // Create UI components
        bookIDField = new JTextField(10);
        titleField = new JTextField(10);
        authorField = new JTextField(10);
        yearField = new JTextField(10);

        addButton = new JButton("Add Book");
        deleteButton = new JButton("Delete Book");
        refreshButton = new JButton("Refresh List");

        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(tableModel);

        // Layout the components
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2));
        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIDField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);
        formPanel.add(addButton);
        formPanel.add(deleteButton);
       
        
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addBook());
        deleteButton.addActionListener(e -> deleteBook());
        refreshButton.addActionListener(e -> refreshBookList());

        // Initial load of book data
        refreshBookList();
    }

    private void initializeDB() {
        try {
            String databaseURL = "jdbc:ucanaccess://C:/Users/PARIS/Documents/Lib.accdb";
            connection = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addBook() {
        try {
            String sql = "INSERT INTO Books (BookID, Title, Author, Year) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, bookIDField.getText());
            statement.setString(2, titleField.getText());
            statement.setString(3, authorField.getText());
            statement.setInt(4, Integer.parseInt(yearField.getText()));
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Added Successfully!");
            refreshBookList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                String bookID = (String) bookTable.getValueAt(selectedRow, 0);
                String sql = "DELETE FROM Books WHERE BookID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, bookID);
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book Deleted Successfully!");
                refreshBookList();
            } else {
                JOptionPane.showMessageDialog(this, "Select a book to delete.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshBookList() {
        try {
            String sql = "SELECT * FROM Books";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            tableModel.setRowCount(0);  // Clear existing data

            while (resultSet.next()) {
                String bookID = resultSet.getString("BookID");
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                int year = resultSet.getInt("Year");

                tableModel.addRow(new Object[]{bookID, title, author, year});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JavaApplication9().setVisible(true));
    }
}
