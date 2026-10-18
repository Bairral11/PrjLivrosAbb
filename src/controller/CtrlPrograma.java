package controller;

import util.ABB;
import model.Livro;
import model.ModelException;

public class CtrlPrograma {
	public static void main(String[] args) {
		ABB arvore = new ABB();

		try {
			arvore.inserir(new Livro(50, "Dom Casmurro", "Machado de Assis", 1899));
			arvore.inserir(new Livro(30, "Vidas Secas", "Graciliano Ramos", 1938));
			arvore.inserir(new Livro(70, "O Cortiço", "Aluísio Azevedo", 1890));
			arvore.inserir(new Livro(20, "Capitães da Areia", "Jorge Amado", 1937));

			System.out.println("Em ordem:");
			for (Livro l : arvore.emOrdem()) {
				System.out.println(l);
			}

			System.out.println("Busca: " + arvore.buscar(30));

			arvore.remover(30);
			System.out.println("Tamanho após remover: " + arvore.getTamanho());

			arvore.inserir(new Livro(50, "Duplicado", "Fulano", 2000)); // lança ModelException
		} catch (ModelException e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}
}