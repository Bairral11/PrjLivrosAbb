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
	
	// Métodos construtores
	public int getChave() {
		return chave;
	}

	public void setChave(int chave) throws ModelException {
		Livro.validarChave(chave);
		this.chave = chave;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) throws ModelException {
		Livro.validarTitulo(titulo);
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) throws ModelException {
		Livro.validarAutor(autor);
		this.autor = autor;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) throws ModelException {
		Livro.validarAno(ano);
		this.ano = ano;
	}
	
	// Métodos de validação
	public static void validarChave(int chave) throws ModelException {
		if (chave <= 0) {
			throw new ModelException("A chave deve ser maior do que 0.");
		}
	}
	
	public static void validarTitulo(String titulo) throws ModelException {
		if (titulo == null || titulo.length() == 0) {
			throw new ModelException("Título não pode ser nulo.");
		}
	}
	
	public static void validarAutor(String autor) throws ModelException {
		if (autor == null || autor.length() == 0) {
			throw new ModelException("Nome do autor não pode ser nulo.");
		}
	}
	
	public static void validarAno(int ano) throws ModelException {
		if (ano <= 0) {
			throw new ModelException("Ano não pode ser menor do que 0.");
		}
	}
}
