package model;

public class Livro {
	private int chave; // id que será usado para alocar o livro na árvore
	private String titulo;
	private String autor;
	private int ano;
	
	public Livro(int chave, String titulo, String autor, int ano) {
		this.chave = chave;
		this.titulo = titulo;
		this.autor = autor;
		this.ano = ano;
	}
}
