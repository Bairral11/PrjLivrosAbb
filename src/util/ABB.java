package util;

import java.util.ArrayList;
import java.util.List;
import model.Livro;
import model.ModelException;

public class ABB {
    private No raiz;
    private int tamanho;

    public ABB() {
        this.raiz = null;
        this.tamanho = 0;
    }

    public int getTamanho() {
        return tamanho;
    }

    // Inseririndo com verificação
    public void inserir(Livro livro) throws ModelException {
        if (livro == null) {
            throw new ModelException("Livro inválido.");
        }
        raiz = inserirRecursivo(raiz, livro);
        tamanho++;
    }

    private No inserirRecursivo(No atual, Livro livro) throws ModelException {
        if (atual == null) {
            return new No(livro);
        }
        if (livro.getChave() < atual.getLivro().getChave()) {
            atual.setEsquerda(inserirRecursivo(atual.getEsquerda(), livro));
        } else if (livro.getChave() > atual.getLivro().getChave()) {
            atual.setDireita(inserirRecursivo(atual.getDireita(), livro));
        } else {
            throw new ModelException("Livro com a chave " + livro.getChave() + " já está cadastrado!");
        }
        return atual;
    }

    // Buscando
    public Livro buscar(int chave) {
        No encontrado = buscarRecursivo(raiz, chave);
        return encontrado != null ? encontrado.getLivro() : null;
    }

    private No buscarRecursivo(No atual, int chave) {
        if (atual == null || atual.getLivro().getChave() == chave) {
            return atual;
        }
        if (chave < atual.getLivro().getChave()) {
            return buscarRecursivo(atual.getEsquerda(), chave);
        }
        return buscarRecursivo(atual.getDireita(), chave);
    }

    // Retornando a lista 
    public List<Livro> emOrdem() {
        List<Livro> lista = new ArrayList<>();
        percorrerEmOrdem(raiz, lista);
        return lista;
    }

    private void percorrerEmOrdem(No atual, List<Livro> lista) {
        if (atual != null) {
            percorrerEmOrdem(atual.getEsquerda(), lista);
            lista.add(atual.getLivro());
            percorrerEmOrdem(atual.getDireita(), lista);
        }
    }

    // Removendo
    public void remover(int chave) throws ModelException {
        if (buscar(chave) == null) {
            throw new ModelException("Livro com chave " + chave + " não encontrado para remoção.");
        }
        raiz = removerRecursivo(raiz, chave);
        tamanho--;
    }

    private No removerRecursivo(No atual, int chave) {
        if (atual == null) return null;

        if (chave < atual.getLivro().getChave()) {
            atual.setEsquerda(removerRecursivo(atual.getEsquerda(), chave));
        } else if (chave > atual.getLivro().getChave()) {
            atual.setDireita(removerRecursivo(atual.getDireita(), chave));
        } else {
            if (atual.getEsquerda() == null) return atual.getDireita();
            if (atual.getDireita() == null) return atual.getEsquerda();

            atual.setLivro(menorValor(atual.getDireita()));
            atual.setDireita(removerRecursivo(atual.getDireita(), atual.getLivro().getChave()));
        }
        return atual;
    }

    private Livro menorValor(No raiz) {
        Livro minv = raiz.getLivro();
        while (raiz.getEsquerda() != null) {
            minv = raiz.getEsquerda().getLivro();
            raiz = raiz.getEsquerda();
        }
        return minv;
    }
}
