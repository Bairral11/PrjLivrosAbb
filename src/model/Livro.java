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
	
	public int getChave() {
		return chave;
	}

	public void setChave(int chave) {
		this.chave = chave;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}
}
