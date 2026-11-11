package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import util.ABB;
import util.No;

/**
 * Painel responsável por desenhar a ABB, animar percursos e permitir
 * navegação por zoom e movimentação.
 */
public class PainelArvore extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int RAIO = 23;
    private static final int ESPACO_X = 76;
    private static final int ESPACO_Y = 88;
    private static final int MARGEM = 42;
    private static final double ZOOM_MINIMO = 0.35;
    private static final double ZOOM_MAXIMO = 2.50;
    private static final double PASSO_ZOOM = 0.15;

    private final ABB arvore;
    private final Map<Integer, Point> posicoes = new HashMap<>();
    private final List<Integer> caminhoAtivo = new ArrayList<>();
    private int indiceAtivo = -1;
    private Integer chaveResultado;
    private Color corResultado = new Color(35, 145, 95);
    private double zoom = 1.0;
    private Point pontoArraste;
    private Point posicaoInicialViewport;

    public PainelArvore(ABB arvore) {
        this.arvore = arvore;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(760, 340));
        setToolTipText("Arraste para movimentar; use os controles para ajustar o zoom.");
        configurarMovimentacao();
        configurarZoomPelaRoda();
    }

    private void configurarMovimentacao() {
        MouseAdapter movimento = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    JViewport viewport = obterViewport();
                    if (viewport != null) {
                        pontoArraste = e.getPoint();
                        posicaoInicialViewport = viewport.getViewPosition();
                        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                JViewport viewport = obterViewport();
                if (viewport == null || pontoArraste == null) return;
                int dx = pontoArraste.x - e.getX();
                int dy = pontoArraste.y - e.getY();
                viewport.setViewPosition(new Point(
                        Math.max(0, posicaoInicialViewport.x + dx),
                        Math.max(0, posicaoInicialViewport.y + dy)));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pontoArraste = null;
                posicaoInicialViewport = null;
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            }
        };
        addMouseListener(movimento);
        addMouseMotionListener(movimento);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void configurarZoomPelaRoda() {
        addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) aumentarZoom();
                else diminuirZoom();
                e.consume();
            }
        });
    }

    private JViewport obterViewport() {
        return (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
    }

    public void aumentarZoom() {
        definirZoom(zoom + PASSO_ZOOM);
    }

    public void diminuirZoom() {
        definirZoom(zoom - PASSO_ZOOM);
    }

    public void zoomPadrao() {
        definirZoom(1.0);
    }

    public int getZoomPercentual() {
        return (int) Math.round(zoom * 100);
    }

    private void definirZoom(double novoZoom) {
        double novo = Math.max(ZOOM_MINIMO, Math.min(ZOOM_MAXIMO, novoZoom));
        if (Math.abs(novo - zoom) < 0.001) return;

        JViewport viewport = obterViewport();
        Point centroAnterior = null;
        Dimension areaVisivel = null;
        if (viewport != null) {
            Point origem = viewport.getViewPosition();
            areaVisivel = viewport.getExtentSize();
            centroAnterior = new Point(origem.x + areaVisivel.width / 2,
                    origem.y + areaVisivel.height / 2);
        }

        double fator = novo / zoom;
        int zoomAnterior = getZoomPercentual();
        zoom = novo;
        firePropertyChange("zoom", zoomAnterior, getZoomPercentual());
        atualizarDimensoes();
        revalidate();
        repaint();

        if (viewport != null && centroAnterior != null) {
            final Point centro = centroAnterior;
            final Dimension area = areaVisivel;
            SwingUtilities.invokeLater(() -> {
                int x = (int) Math.round(centro.x * fator - area.width / 2.0);
                int y = (int) Math.round(centro.y * fator - area.height / 2.0);
                viewport.setViewPosition(new Point(Math.max(0, x), Math.max(0, y)));
            });
        }
    }

    public void centralizar() {
        JViewport viewport = obterViewport();
        if (viewport == null) return;

        Dimension area = viewport.getExtentSize();
        Dimension preferida = getPreferredSize();
        if (preferida.width < area.width || preferida.height < area.height) {
            setPreferredSize(new Dimension(Math.max(preferida.width, area.width),
                    Math.max(preferida.height, area.height)));
            revalidate();
        }

        SwingUtilities.invokeLater(() -> {
            Dimension view = viewport.getViewSize();
            Dimension areaVisivel = viewport.getExtentSize();
            int x = Math.max(0, (view.width - areaVisivel.width) / 2);
            int y = Math.max(0, (view.height - areaVisivel.height) / 2);
            viewport.setViewPosition(new Point(x, y));
        });
    }

    public void atualizar() {
        atualizarDimensoes();
        revalidate();
        repaint();
        SwingUtilities.invokeLater(this::centralizar);
    }

    private void atualizarDimensoes() {
        int quantidade = Math.max(1, arvore.getTamanho());
        long larguraBase = Math.max(760L, (long) quantidade * ESPACO_X + MARGEM * 2L);
        long alturaBase = Math.max(340L, (long) (altura(arvore.getRaiz()) + 1) * ESPACO_Y
                + MARGEM * 2L);
        int largura = (int) Math.min(Integer.MAX_VALUE / 4, Math.ceil(larguraBase * zoom));
        int altura = (int) Math.min(Integer.MAX_VALUE / 4, Math.ceil(alturaBase * zoom));
        setPreferredSize(new Dimension(largura, altura));
    }

    public void animarCaminho(List<Integer> caminho, Integer resultado, Color cor, Runnable aoFinal) {
        caminhoAtivo.clear();
        caminhoAtivo.addAll(caminho);
        indiceAtivo = -1;
        chaveResultado = resultado;
        corResultado = cor;
        repaint();

        if (caminhoAtivo.isEmpty()) {
            if (aoFinal != null) aoFinal.run();
            return;
        }

        Timer timer = new Timer(420, null);
        timer.addActionListener(e -> {
            indiceAtivo++;
            repaint();
            if (indiceAtivo >= caminhoAtivo.size()) {
                timer.stop();
                if (aoFinal != null) aoFinal.run();
            }
        });
        timer.setInitialDelay(80);
        timer.start();
    }

    public void limparDestaques() {
        caminhoAtivo.clear();
        indiceAtivo = -1;
        chaveResultado = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        posicoes.clear();
        No raiz = arvore.getRaiz();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(zoom, zoom);

        if (raiz == null) {
            g2.setColor(new Color(125, 135, 148));
            g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
            String mensagem = "Árvore vazia — adicione um livro para começar";
            int x = Math.max(20, (int) ((getWidth() / zoom
                    - g2.getFontMetrics().stringWidth(mensagem)) / 2));
            g2.drawString(mensagem, x, 55);
            g2.dispose();
            return;
        }

        int[] ordem = { 0 };
        calcularPosicoes(raiz, 0, ordem);
        g2.setStroke(new BasicStroke(2f));
        desenharLigacoes(g2, raiz);
        Set<Integer> visitados = new HashSet<>();
        if (indiceAtivo >= 0) {
            visitados.addAll(caminhoAtivo.subList(0,
                    Math.min(indiceAtivo + 1, caminhoAtivo.size())));
        }
        desenharNos(g2, raiz, visitados);
        g2.dispose();
    }

    private void calcularPosicoes(No raiz, int nivelInicial, int[] ordem) {
        Deque<EntradaNo> pilha = new ArrayDeque<>();
        No atual = raiz;
        int nivel = nivelInicial;

        while (atual != null || !pilha.isEmpty()) {
            while (atual != null) {
                pilha.push(new EntradaNo(atual, nivel));
                atual = atual.getEsquerda();
                nivel++;
            }
            EntradaNo entrada = pilha.pop();
            int chave = entrada.no.getLivro().getChave();
            posicoes.put(chave, new Point(MARGEM + ordem[0] * ESPACO_X,
                    MARGEM + entrada.nivel * ESPACO_Y));
            ordem[0]++;
            atual = entrada.no.getDireita();
            nivel = entrada.nivel + 1;
        }
    }

    private void desenharLigacoes(Graphics2D g, No raiz) {
        Deque<No> pilha = new ArrayDeque<>();
        pilha.push(raiz);
        while (!pilha.isEmpty()) {
            No no = pilha.pop();
            Point pai = posicoes.get(no.getLivro().getChave());
            if (no.getEsquerda() != null) {
                desenharLigacao(g, pai, no.getEsquerda());
                pilha.push(no.getEsquerda());
            }
            if (no.getDireita() != null) {
                desenharLigacao(g, pai, no.getDireita());
                pilha.push(no.getDireita());
            }
        }
    }

    private void desenharLigacao(Graphics2D g, Point pai, No filho) {
        if (filho == null) return;
        Point destino = posicoes.get(filho.getLivro().getChave());
        g.setColor(new Color(155, 168, 184));
        g.drawLine(pai.x, pai.y + RAIO, destino.x, destino.y - RAIO);
    }

    private void desenharNos(Graphics2D g, No raiz, Set<Integer> visitados) {
        Deque<No> pilha = new ArrayDeque<>();
        pilha.push(raiz);
        while (!pilha.isEmpty()) {
            No no = pilha.pop();
            int chave = no.getLivro().getChave();
            Point p = posicoes.get(chave);
            boolean visitado = visitados.contains(chave);
            boolean resultado = chaveResultado != null && chaveResultado.intValue() == chave;
            Color preenchimento = resultado ? corResultado
                    : visitado ? new Color(245, 183, 65) : new Color(54, 105, 160);

            g.setColor(new Color(25, 45, 65, 28));
            g.fillOval(p.x - RAIO + 2, p.y - RAIO + 3, RAIO * 2, RAIO * 2);
            g.setColor(preenchimento);
            g.fillOval(p.x - RAIO, p.y - RAIO, RAIO * 2, RAIO * 2);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(p.x - RAIO, p.y - RAIO, RAIO * 2, RAIO * 2);
            g.setFont(getFont().deriveFont(Font.BOLD, 12f));
            String texto = String.valueOf(chave);
            int largura = g.getFontMetrics().stringWidth(texto);
            g.drawString(texto, p.x - largura / 2, p.y + 4);

            if (no.getDireita() != null) pilha.push(no.getDireita());
            if (no.getEsquerda() != null) pilha.push(no.getEsquerda());
        }
    }

    private int altura(No raiz) {
        if (raiz == null) return -1;
        Deque<EntradaNo> pilha = new ArrayDeque<>();
        pilha.push(new EntradaNo(raiz, 0));
        int maiorNivel = 0;
        while (!pilha.isEmpty()) {
            EntradaNo entrada = pilha.pop();
            maiorNivel = Math.max(maiorNivel, entrada.nivel);
            if (entrada.no.getEsquerda() != null)
                pilha.push(new EntradaNo(entrada.no.getEsquerda(), entrada.nivel + 1));
            if (entrada.no.getDireita() != null)
                pilha.push(new EntradaNo(entrada.no.getDireita(), entrada.nivel + 1));
        }
        return maiorNivel;
    }

    private static class EntradaNo {
        private final No no;
        private final int nivel;

        private EntradaNo(No no, int nivel) {
            this.no = no;
            this.nivel = nivel;
        }
    }
}
