package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import model.Livro;
import model.ModelException;
import util.ABB;
import util.No;

/**
 * Interface Swing da biblioteca, integrada à ABB e à sua representação gráfica.
 */
public class TelaBiblioteca extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ABB arvore;
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final JLabel lblTotal;
    private final JLabel lblStatus;
    private final JLabel lblZoom;
    private final PainelArvore painelArvore;
    private boolean animacaoEmCurso;

    public TelaBiblioteca() {
        super("Biblioteca ABB | Gerenciamento de livros");
        arvore = new ABB();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 680));
        setSize(1100, 790);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(14, 14));
        raiz.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
        raiz.setBackground(new Color(245, 247, 250));
        setContentPane(raiz);

        JLabel titulo = new JLabel("Biblioteca Virtual");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(new Color(30, 45, 65));
        JLabel subtitulo = new JLabel("Gerencie livros e acompanhe visualmente a Árvore Binária de Busca");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(100, 112, 128));
        JPanel cabecalho = new JPanel(new GridLayout(2, 1, 0, 4));
        cabecalho.setOpaque(false);
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);
        raiz.add(cabecalho, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new Object[] { "ID", "Título", "Autor", "Ano" }, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);

        painelArvore = new PainelArvore(arvore);
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Livros cadastrados"));
        JScrollPane scrollArvore = new JScrollPane(painelArvore);
        scrollArvore.setBorder(BorderFactory.createTitledBorder("Representação gráfica da ABB"));
        scrollArvore.getViewport().setBackground(Color.WHITE);

        JButton btnZoomMenos = botao("−", new Color(90, 105, 125));
        JButton btnZoomMais = botao("+", new Color(55, 95, 150));
        JButton btnZoomPadrao = botao("100%", new Color(90, 105, 125));
        JButton btnCentralizar = botao("Centralizar", new Color(35, 120, 90));
        lblZoom = new JLabel("100%");
        lblZoom.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblZoom.setForeground(new Color(70, 85, 100));
        painelArvore.addPropertyChangeListener("zoom", e -> atualizarIndicadorZoom());

        JPanel controlesArvore = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        controlesArvore.setOpaque(false);
        controlesArvore.add(new JLabel("Zoom:"));
        controlesArvore.add(btnZoomMenos);
        controlesArvore.add(lblZoom);
        controlesArvore.add(btnZoomMais);
        controlesArvore.add(btnZoomPadrao);
        controlesArvore.add(btnCentralizar);

        JPanel areaArvore = new JPanel(new BorderLayout(0, 4));
        areaArvore.setOpaque(false);
        areaArvore.add(controlesArvore, BorderLayout.NORTH);
        areaArvore.add(scrollArvore, BorderLayout.CENTER);

        btnZoomMenos.addActionListener(e -> alterarZoom(-1));
        btnZoomMais.addActionListener(e -> alterarZoom(1));
        btnZoomPadrao.addActionListener(e -> {
            painelArvore.zoomPadrao();
            atualizarIndicadorZoom();
        });
        btnCentralizar.addActionListener(e -> painelArvore.centralizar());

        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabela, areaArvore);
        divisao.setResizeWeight(0.43);
        divisao.setBorder(null);
        raiz.add(divisao, BorderLayout.CENTER);

        JButton btnAdicionar = botao("＋ Adicionar livro", new Color(35, 120, 90));
        JButton btnRemover = botao("Remover selecionado", new Color(180, 65, 65));
        JButton btnBuscar = botao("Buscar por ID", new Color(55, 95, 150));
        JButton btnAtualizar = botao("Atualizar lista", new Color(90, 105, 125));
        JButton btnMassaTeste = botao("Carregar 50 nós de teste", new Color(115, 85, 155));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setOpaque(false);
        acoes.add(btnAdicionar);
        acoes.add(btnRemover);
        acoes.add(btnBuscar);
        acoes.add(btnAtualizar);
        acoes.add(btnMassaTeste);

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
        btnMassaTeste.addActionListener(e -> carregarMassaTeste());
        atualizarTabela();
    }

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
        if (animacaoEmCurso) return;
        JTextField id = new JTextField();
        JTextField titulo = new JTextField();
        JTextField autor = new JTextField();
        JTextField ano = new JTextField();
        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 10));
        formulario.setPreferredSize(new Dimension(360, 150));
        formulario.add(new JLabel("ID / chave:")); formulario.add(id);
        formulario.add(new JLabel("Título:")); formulario.add(titulo);
        formulario.add(new JLabel("Autor:")); formulario.add(autor);
        formulario.add(new JLabel("Ano de publicação:")); formulario.add(ano);

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

            List<Integer> caminho = caminhoAte(chave);
            arvore.inserir(new Livro(chave, nome, escritor, anoPublicacao));
            atualizarTabela();
            caminho.add(chave);
            animar(caminho, chave, new Color(35, 145, 95),
                    "Inserção concluída: livro " + chave + " adicionado.", null);
        } catch (NumberFormatException ex) {
            erro("ID e ano devem ser números inteiros.");
        } catch (ModelException ex) {
            erro(ex.getMessage());
        }
    }

    /**
     * Insere 50 livros de teste com chaves únicas, em ordem de medianas,
     * formando uma árvore de teste aproximadamente balanceada.
     * Os livros já cadastrados são preservados.
     */
    private void carregarMassaTeste() {
        if (animacaoEmCurso) return;

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Serão adicionados 50 livros de teste à árvore atual. "
                + "Os livros já cadastrados serão preservados. Deseja continuar?",
                "Carregar massa de teste", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) return;

        List<Integer> chaves = new ArrayList<>();
        int candidata = 1000;
        while (chaves.size() < 50) {
            if (candidata <= 0) {
                erro("Não foi possível encontrar chaves disponíveis para a massa de teste.");
                return;
            }
            if (arvore.buscar(candidata) == null) {
                chaves.add(candidata);
            }
            if (candidata == Integer.MAX_VALUE && chaves.size() < 50) {
                erro("Não há chaves inteiras disponíveis para completar a massa de teste.");
                return;
            }
            candidata++;
        }

        Deque<int[]> intervalos = new ArrayDeque<>();
        intervalos.add(new int[] { 0, chaves.size() - 1 });
        int indiceLivro = 1;
        try {
            while (!intervalos.isEmpty()) {
                int[] intervalo = intervalos.removeFirst();
                int inicio = intervalo[0];
                int fim = intervalo[1];
                if (inicio > fim) continue;

                int meio = inicio + (fim - inicio) / 2;
                int chave = chaves.get(meio);
                Livro livro = new Livro(chave,
                        "Livro de teste " + indiceLivro,
                        "Autor de teste " + indiceLivro,
                        1980 + (indiceLivro % 47));
                arvore.inserir(livro);
                indiceLivro++;

                intervalos.addLast(new int[] { inicio, meio - 1 });
                intervalos.addLast(new int[] { meio + 1, fim });
            }

            atualizarTabela();
            painelArvore.limparDestaques();
            status("Massa de teste carregada: 50 livros adicionados. Total na árvore: "
                    + arvore.getTamanho() + ".");
            JOptionPane.showMessageDialog(this,
                    "50 livros de teste foram adicionados com sucesso.\n"
                    + "Total atual de nós: " + arvore.getTamanho(),
                    "Massa de teste carregada", JOptionPane.INFORMATION_MESSAGE);
        } catch (ModelException ex) {
            erro("Falha ao carregar a massa de teste: " + ex.getMessage());
        }
    }

    private void removerSelecionado() {
        if (animacaoEmCurso) return;
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

        List<Integer> caminho = caminhoAte(chave);
        animar(caminho, chave, new Color(190, 75, 75), "Percorrendo a árvore para remover " + chave + ".", () -> {
            try {
                arvore.remover(chave);
                atualizarTabela();
                painelArvore.limparDestaques();
                status("Livro " + chave + " removido; estrutura da árvore atualizada.");
            } catch (ModelException ex) {
                erro(ex.getMessage());
            }
        });
    }

    private void buscarLivro() {
        if (animacaoEmCurso) return;
        String entrada = JOptionPane.showInputDialog(this, "Informe o ID do livro:",
                "Buscar livro", JOptionPane.QUESTION_MESSAGE);
        if (entrada == null) return;
        try {
            int chave = Integer.parseInt(entrada.trim());
            List<Integer> caminho = caminhoAte(chave);
            Livro livro = arvore.buscar(chave);
            animar(caminho, livro == null ? null : chave,
                    livro == null ? new Color(190, 75, 75) : new Color(35, 145, 95),
                    "Busca concluída para o ID " + chave + ".", () -> {
                if (livro == null) {
                    status("Nenhum livro encontrado para o ID " + chave + ".");
                    JOptionPane.showMessageDialog(this, "Livro não encontrado.",
                            "Resultado da busca", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    selecionarLinha(chave);
                    status("Livro encontrado: " + livro.getTitulo());
                    JOptionPane.showMessageDialog(this,
                            "ID: " + livro.getChave() + "\nTítulo: " + livro.getTitulo()
                            + "\nAutor: " + livro.getAutor() + "\nAno: " + livro.getAno(),
                            "Livro encontrado", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        } catch (NumberFormatException ex) {
            erro("Informe um ID numérico válido.");
        }
    }

    private List<Integer> caminhoAte(int chave) {
        List<Integer> caminho = new ArrayList<>();
        No atual = arvore.getRaiz();
        while (atual != null) {
            int atualChave = atual.getLivro().getChave();
            caminho.add(atualChave);
            if (chave == atualChave) break;
            atual = chave < atualChave ? atual.getEsquerda() : atual.getDireita();
        }
        return caminho;
    }

    private void animar(List<Integer> caminho, Integer resultado, Color cor,
            String mensagem, Runnable aoFinal) {
        animacaoEmCurso = true;
        status(mensagem);
        painelArvore.animarCaminho(caminho, resultado, cor, () -> {
            animacaoEmCurso = false;
            if (aoFinal != null) aoFinal.run();
        });
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
        painelArvore.atualizar();
        if (total == 0) status("A biblioteca está vazia. Cadastre o primeiro livro.");
    }

    private void alterarZoom(int direcao) {
        if (direcao > 0) painelArvore.aumentarZoom();
        else painelArvore.diminuirZoom();
        atualizarIndicadorZoom();
    }

    private void atualizarIndicadorZoom() {
        lblZoom.setText(painelArvore.getZoomPercentual() + "%");
    }

    private void status(String mensagem) { lblStatus.setText(mensagem); }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
        status(mensagem);
    }

    public static void abrir() {
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
