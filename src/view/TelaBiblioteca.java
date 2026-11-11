package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import model.Livro;
import model.ModelException;
import util.ABB;

/**
 * Interface grafica Swing para interagir com a arvore binaria de busca de livros.
 * A tela utiliza diretamente os metodos publicos da classe ABB.
 */
public class TelaBiblioteca extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ABB arvore;
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final JLabel lblTotal;
    private final JLabel lblStatus;

    
    
    
 // Layout da Tela________________________________________________________________________________________________________________________________________________________________
    
    public TelaBiblioteca() {           
        super("Biblioteca ABB | Gerenciamento de livros");
        this.arvore = new ABB();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 560));
        setSize(980, 650);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(16, 16));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        raiz.setBackground(new Color(245, 247, 250));
        setContentPane(raiz);

        JLabel titulo = new JLabel("Biblioteca Virtual");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(new Color(30, 45, 65));
        JLabel subtitulo = new JLabel("Gerencie livros usando uma Árvore Binária de Busca");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(100, 112, 128));

        JPanel cabecalho = new JPanel(new GridLayout(2, 1, 0, 4));
        cabecalho.setOpaque(false);
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);
        raiz.add(cabecalho, BorderLayout.NORTH);

        // Tabela de Dados_______________________________________________________________________________________________________________________________________________________
        
        modeloTabela = new DefaultTableModel(new Object[] { "ID", "Título", "Autor", "Ano" }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 232)));
        raiz.add(scroll, BorderLayout.CENTER);
        
        //Botões no Layout___________________________________________________________________________________________________________________________________________________________

        JButton btnAdicionar = botao("＋ Adicionar livro", new Color(35, 120, 90));
        JButton btnRemover = botao("Remover selecionado", new Color(180, 65, 65));
        JButton btnBuscar = botao("Buscar por ID", new Color(55, 95, 150));
        JButton btnAtualizar = botao("Atualizar lista", new Color(90, 105, 125));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setOpaque(false);
        acoes.add(btnAdicionar);
        acoes.add(btnRemover);
        acoes.add(btnBuscar);
        acoes.add(btnAtualizar);

        lblTotal = new JLabel("0 livros cadastrados");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotal.setForeground(new Color(35, 75, 110));

        lblStatus = new JLabel("Pronto para começar.");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(105, 115, 125));

        JPanel rodape = new JPanel(new BorderLayout(8, 10));
        rodape.setOpaque(false);
        rodape.add(acoes, BorderLayout.NORTH);
        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.add(lblTotal, BorderLayout.WEST);
        info.add(lblStatus, BorderLayout.EAST);
        rodape.add(info, BorderLayout.SOUTH);
        raiz.add(rodape, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> adicionarLivro());
        btnRemover.addActionListener(e -> removerSelecionado());
        btnBuscar.addActionListener(e -> buscarLivro());
        btnAtualizar.addActionListener(e -> atualizarTabela());

        atualizarTabela();
    }

    //Métodos dos Botões _____________________________________________________________________________________________________________________________________________________________
    
    private JButton botao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setFont(new Font("SansSerif", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        botao.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return botao;
    }

    private void adicionarLivro() {
        JTextField id = new JTextField();
        JTextField titulo = new JTextField();
        JTextField autor = new JTextField();
        JTextField ano = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 10));
        formulario.setPreferredSize(new Dimension(360, 150));
        formulario.add(new JLabel("ID / chave:"));
        formulario.add(id);
        formulario.add(new JLabel("Título:"));
        formulario.add(titulo);
        formulario.add(new JLabel("Autor:"));
        formulario.add(autor);
        formulario.add(new JLabel("Ano de publicação:"));
        formulario.add(ano);

        int opcao = JOptionPane.showConfirmDialog(this, formulario, "Cadastrar livro",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) return;

        try {
            int chave = Integer.parseInt(id.getText().trim());
            int anoPublicacao = Integer.parseInt(ano.getText().trim());
            String nome = titulo.getText().trim();
            String escritor = autor.getText().trim();

            Livro.validarChave(chave);
            Livro.validarTitulo(nome);
            Livro.validarAutor(escritor);
            Livro.validarAno(anoPublicacao);

            arvore.inserir(new Livro(chave, nome, escritor, anoPublicacao));
            atualizarTabela();
            status("Livro cadastrado com sucesso.");
            JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso!",
                    "Cadastro concluído", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            erro("ID e ano devem ser números inteiros.");
        } catch (ModelException ex) {
            erro(ex.getMessage());
        }
    }

    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            erro("Selecione um livro na tabela para removê-lo.");
            return;
        }

        int chave = (int) modeloTabela.getValueAt(linha, 0);
        String nome = String.valueOf(modeloTabela.getValueAt(linha, 1));
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover \"" + nome + "\" (ID " + chave + ")?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) return;

        try {
            arvore.remover(chave);
            atualizarTabela();
            status("Livro " + chave + " removido.");
        } catch (ModelException ex) {
            erro(ex.getMessage());
        }
    }

    private void buscarLivro() {
        String entrada = JOptionPane.showInputDialog(this, "Informe o ID do livro:",
                "Buscar livro", JOptionPane.QUESTION_MESSAGE);
        if (entrada == null) return;

        try {
            int chave = Integer.parseInt(entrada.trim());
            Livro livro = arvore.buscar(chave);
            if (livro == null) {
                status("Nenhum livro encontrado para o ID " + chave + ".");
                JOptionPane.showMessageDialog(this, "Livro não encontrado.",
                        "Resultado da busca", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            selecionarLinha(chave);
            status("Livro encontrado: " + livro.getTitulo());
            JOptionPane.showMessageDialog(this,
                    "ID: " + livro.getChave() + "\nTítulo: " + livro.getTitulo()
                    + "\nAutor: " + livro.getAutor() + "\nAno: " + livro.getAno(),
                    "Livro encontrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            erro("Informe um ID numérico válido.");
        }
    }

    private void selecionarLinha(int chave) {
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            if ((int) modeloTabela.getValueAt(i, 0) == chave) {
                tabela.setRowSelectionInterval(i, i);
                tabela.scrollRectToVisible(tabela.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        List<Livro> livros = arvore.emOrdem();
        for (Livro livro : livros) {
            modeloTabela.addRow(new Object[] {
                livro.getChave(), livro.getTitulo(), livro.getAutor(), livro.getAno()
            });
        }
        int total = arvore.getTamanho();
        lblTotal.setText(total + (total == 1 ? " livro cadastrado" : " livros cadastrados"));
        if (total == 0) status("A biblioteca está vazia. Cadastre o primeiro livro.");
    }

    private void status(String mensagem) {
        lblStatus.setText(mensagem);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
        status(mensagem);
    }

    public static void main(String[] args) {        abrir();   }   public static void abrir() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Mantém o visual padrão caso o Nimbus não esteja disponível.
        }
        SwingUtilities.invokeLater(() -> new TelaBiblioteca().setVisible(true));
    }
}
