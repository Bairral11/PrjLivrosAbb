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

	public boolean estaVazia() {
		return raiz == null;
	}

	public int getTamanho() {
		return tamanho;
	}

	// Inserir
	public void inserir(Livro livro) throws ModelException {
		if (livro == null) {
			throw new ModelException("O livro não pode ser nulo.");
		}
		raiz = inserir(raiz, livro);
		tamanho++;
	}

	private No inserir(No atual, Livro livro) throws ModelException {
		if (atual == null) {
			return new No(livro);
		}
		if (livro.getChave() < atual.getLivro().getChave()) {
			atual.setEsquerda(inserir(atual.getEsquerda(), livro));
		} else if (livro.getChave() > atual.getLivro().getChave()) {
			atual.setDireita(inserir(atual.getDireita(), livro));
		} else {
			throw new ModelException("Já existe um livro com a chave " + livro.getChave() + ".");
		}
		return atual;
	}

	// Buscar
	public Livro buscar(int chave) throws ModelException {
		No atual = raiz;
		while (atual != null) {
			if (chave == atual.getLivro().getChave()) {
				return atual.getLivro();
			}
			atual = (chave < atual.getLivro().getChave()) ? atual.getEsquerda() : atual.getDireita();
		}
		throw new ModelException("Livro com a chave " + chave + " não encontrado.");
	}

	// Remover
	public void remover(int chave) throws ModelException {
		raiz = remover(raiz, chave);
		tamanho--;
	}

	private No remover(No atual, int chave) throws ModelException {
		if (atual == null) {
			throw new ModelException("Livro com a chave " + chave + " não encontrado.");
		}
		if (chave < atual.getLivro().getChave()) {
			atual.setEsquerda(remover(atual.getEsquerda(), chave));
		} else if (chave > atual.getLivro().getChave()) {
			atual.setDireita(remover(atual.getDireita(), chave));
		} else {
			if (atual.getEsquerda() == null) {
				return atual.getDireita();
			}
			if (atual.getDireita() == null) {
				return atual.getEsquerda();
			}
			No sucessor = menor(atual.getDireita());
			atual.setLivro(sucessor.getLivro());
			atual.setDireita(removerMenor(atual.getDireita()));
		}
		return atual;
	}

	private No menor(No no) {
		while (no.getEsquerda() != null) {
			no = no.getEsquerda();
		}
		return no;
	}

	private No removerMenor(No no) {
		if (no.getEsquerda() == null) {
			return no.getDireita();
		}
		no.setEsquerda(removerMenor(no.getEsquerda()));
		return no;
	}

	// ---------- Percursos ----------
	public List<Livro> emOrdem() {
		List<Livro> lista = new ArrayList<>();
		emOrdem(raiz, lista);
		return lista;
	}

	private void emOrdem(No no, List<Livro> lista) {
		if (no != null) {
			emOrdem(no.getEsquerda(), lista);
			lista.add(no.getLivro());
			emOrdem(no.getDireita(), lista);
		}
	}

	// ---------- Altura ----------
	public int altura() {
		return altura(raiz);
	}

	private int altura(No no) {
		if (no == null) {
			return 0;
		}
		return 1 + Math.max(altura(no.getEsquerda()), altura(no.getDireita()));
	}
}