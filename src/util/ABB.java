package util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

    /** Retorna a raiz para consulta e representação visual da árvore. */
    public No getRaiz() {
        return raiz;
    }

    /** Insere um livro sem usar recursão, evitando estouro de pilha em árvores altas. */
    public void inserir(Livro livro) throws ModelException {
        if (livro == null) {
            throw new ModelException("Livro inválido.");
        }

        No novo = new No(livro);
        if (raiz == null) {
            raiz = novo;
            tamanho++;
            return;
        }

        No atual = raiz;
        while (true) {
            int chaveAtual = atual.getLivro().getChave();
            int novaChave = livro.getChave();

            if (novaChave < chaveAtual) {
                if (atual.getEsquerda() == null) {
                    atual.setEsquerda(novo);
                    tamanho++;
                    return;
                }
                atual = atual.getEsquerda();
            } else if (novaChave > chaveAtual) {
                if (atual.getDireita() == null) {
                    atual.setDireita(novo);
                    tamanho++;
                    return;
                }
                atual = atual.getDireita();
            } else {
                throw new ModelException("Livro com a chave " + novaChave
                        + " já está cadastrado!");
            }
        }
    }

    /** Busca iterativa para suportar árvores desbalanceadas e de grande altura. */
    public Livro buscar(int chave) {
        No atual = raiz;
        while (atual != null) {
            int chaveAtual = atual.getLivro().getChave();
            if (chave == chaveAtual) return atual.getLivro();
            atual = chave < chaveAtual ? atual.getEsquerda() : atual.getDireita();
        }
        return null;
    }

    /** Percurso em ordem iterativo: retorna os livros ordenados pela chave. */
    public List<Livro> emOrdem() {
        List<Livro> lista = new ArrayList<>();
        Deque<No> pilha = new ArrayDeque<>();
        No atual = raiz;

        while (atual != null || !pilha.isEmpty()) {
            while (atual != null) {
                pilha.push(atual);
                atual = atual.getEsquerda();
            }
            atual = pilha.pop();
            lista.add(atual.getLivro());
            atual = atual.getDireita();
        }
        return lista;
    }

    /** Remove um livro preservando as regras da ABB, sem chamadas recursivas. */
    public void remover(int chave) throws ModelException {
        No pai = null;
        No atual = raiz;

        while (atual != null && atual.getLivro().getChave() != chave) {
            pai = atual;
            atual = chave < atual.getLivro().getChave()
                    ? atual.getEsquerda() : atual.getDireita();
        }

        if (atual == null) {
            throw new ModelException("Livro com chave " + chave
                    + " não encontrado para remoção.");
        }

        // Com dois filhos, substitui pelo sucessor em ordem.
        if (atual.getEsquerda() != null && atual.getDireita() != null) {
            No paiSucessor = atual;
            No sucessor = atual.getDireita();
            while (sucessor.getEsquerda() != null) {
                paiSucessor = sucessor;
                sucessor = sucessor.getEsquerda();
            }

            atual.setLivro(sucessor.getLivro());
            No filhoSucessor = sucessor.getDireita();
            if (paiSucessor.getEsquerda() == sucessor) {
                paiSucessor.setEsquerda(filhoSucessor);
            } else {
                paiSucessor.setDireita(filhoSucessor);
            }
        } else {
            // Com zero ou um filho, conecta o pai diretamente ao substituto.
            No substituto = atual.getEsquerda() != null
                    ? atual.getEsquerda() : atual.getDireita();

            if (pai == null) {
                raiz = substituto;
            } else if (pai.getEsquerda() == atual) {
                pai.setEsquerda(substituto);
            } else {
                pai.setDireita(substituto);
            }
        }

        tamanho--;
    }
}
