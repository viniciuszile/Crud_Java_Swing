/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.swing;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class Swing {

    private static JTextField nomeField;
    private static JTextField idadeField;
    private static JTextField cpfField;
    private static JTextArea infoArea;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Conexão com o Banco de Dados");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JButton button1 = new JButton("Acessar Informações dos Clientes");
        JButton button2 = new JButton("Cadastrar Novo Cliente");
        JButton button3 = new JButton("Deletar Cliente");
        JButton button4 = new JButton("Atualizar Cliente");
        JButton button5 = new JButton("Sair");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);
        buttonPanel.add(button5);
        panel.add(buttonPanel);

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane);

        button1.addActionListener(e -> acessarInformacoes());
        button2.addActionListener(e -> cadastrarCliente());
        button3.addActionListener(e -> deletarCliente());
        button4.addActionListener(e -> atualizarCliente());
        button5.addActionListener(e -> System.exit(0));

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        atualizarLista();
    }

    private static void acessarInformacoes() {
        infoArea.setText("");
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nome");
        model.addColumn("Idade");
        model.addColumn("CPF");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/zile", "root", "");
            ResultSet rsexemplo = conexao.createStatement().executeQuery("SELECT * FROM exemplo");

            while (rsexemplo.next()) {
                String nome = rsexemplo.getString("nome");
                int idade = rsexemplo.getInt("idade");
                long cpf = rsexemplo.getLong("CPF");
                model.addRow(new Object[]{nome, idade, cpf});
            }
            table.setModel(model);
            JScrollPane scrollPane = new JScrollPane(table);

        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Driver não encontrado");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão com o banco: " + ex.getMessage());
        }
    }

    private static void cadastrarCliente() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/zile", "root", "");

            String nome = JOptionPane.showInputDialog("Insira o nome do cliente:");
            int idade = Integer.parseInt(JOptionPane.showInputDialog("Insira a idade do cliente:"));
            long cpf = Long.parseLong(JOptionPane.showInputDialog("Insira o CPF do cliente:"));

            String comandoSql = "INSERT INTO exemplo (nome, idade, CPF) VALUES ('" + nome + "', " + idade + ", " + cpf + ")";
            conexao.createStatement().executeUpdate(comandoSql);

            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
            atualizarLista();

        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Driver não encontrado");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão com o banco: " + ex.getMessage());
        }
    }

    private static void deletarCliente() {
        String cpf = JOptionPane.showInputDialog(null, "Digite o CPF do cliente a ser deletado:", "Deletar cliente", JOptionPane.PLAIN_MESSAGE);

        if (cpf != null && !cpf.isEmpty()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/zile", "root", "");
                PreparedStatement pstmt = conexao.prepareStatement("SELECT * FROM exemplo WHERE CPF=?");
                pstmt.setLong(1, Long.parseLong(cpf));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String nome = rs.getString("nome");
                    int idade = rs.getInt("idade");
                    long cpfToDelete = rs.getLong("CPF");

                    int option = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja deletar o seguinte cliente?\n\nNome: " + nome + "\nIdade: " + idade + "\nCPF: " + cpfToDelete, "Deletar cliente", JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        PreparedStatement pstmt2 = conexao.prepareStatement("DELETE FROM exemplo WHERE CPF=?");
                        pstmt2.setLong(1, cpfToDelete);
                        pstmt2.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Cliente deletado com sucesso!", "Deletar cliente", JOptionPane.INFORMATION_MESSAGE);

                        // atualiza a lista de usuários após a exclusão
                        atualizarLista();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Cliente não encontrado", "Deletar cliente", JOptionPane.ERROR_MESSAGE);
                }

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Driver não encontrado");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de conexão com o banco: " + ex.getMessage());
            }
        }
    }

    private static void atualizarLista() {
        infoArea.setText(""); // limpa o conteúdo da infoArea

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/zile", "root", "");
            ResultSet rsexemplo = conexao.createStatement().executeQuery("SELECT * FROM exemplo");

            while (rsexemplo.next()) {
                String nome = rsexemplo.getString("nome");
                int idade = rsexemplo.getInt("idade");
                long cpf = rsexemplo.getLong("CPF");
                String info = "Nome: " + nome + " - Idade: " + idade + " - CPF: " + cpf + "\n";
                infoArea.append(info);
            }

        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Driver não encontrado");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão com o banco: " + ex.getMessage());
        }
    }

    private static void atualizarCliente() {
        String cpf = JOptionPane.showInputDialog(null, "Digite o CPF do cliente a ser editado:", "Editar cliente", JOptionPane.PLAIN_MESSAGE);

        if (cpf != null && !cpf.isEmpty()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/zile", "root", "");
                PreparedStatement pstmt = conexao.prepareStatement("SELECT * FROM exemplo WHERE CPF=?");
                pstmt.setLong(1, Long.parseLong(cpf));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String nome = rs.getString("nome");
                    int idade = rs.getInt("idade");

                    String novoNome = JOptionPane.showInputDialog("Insira o novo nome do cliente:", nome);
                    int novaIdade = Integer.parseInt(JOptionPane.showInputDialog("Insira a nova idade do cliente:", idade));

                    PreparedStatement pstmt2 = conexao.prepareStatement("UPDATE exemplo SET nome=?, idade=? WHERE CPF=?");
                    pstmt2.setString(1, novoNome);
                    pstmt2.setInt(2, novaIdade);
                    pstmt2.setLong(3, Long.parseLong(cpf));
                    pstmt2.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Cliente editado com sucesso!");
                    atualizarLista();
                } else {
                    JOptionPane.showMessageDialog(null, "CPF não encontrado!");
                }

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Driver não encontrado");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de conexão com o banco: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "CPF inválido!");
            }
        }
    }
}
