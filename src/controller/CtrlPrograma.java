package controller;

import java.util.Scanner;
import util.ABB;
import model.Livro;
import model.ModelException;

public class CtrlPrograma {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ABB arvore = new ABB();
        int opcao;

        do {
            System.out.println("\n=== BIBLIOTECA VIRTUAL - ABB ===");
            System.out.println("1. Inserir Livro");
            System.out.println("2. Remover Livro");
            System.out.println("3. Buscar Livro");
            System.out.println("4. Listar Livros (Em Ordem)");
            System.out.println("5. Ver Tamanho da Árvore");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    try {
                        System.out.print("Digite a chave (ID) do livro: ");
                        int chave = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Digite o título do livro: ");
                        String titulo = scanner.nextLine();

                        System.out.print("Digite o autor do livro: ");
                        String autor = scanner.nextLine();

                        System.out.print("Digite o ano de publicação: ");
                        int ano = scanner.nextInt();
                        scanner.nextLine();

                        Livro.validarChave(chave);
                        Livro.validarTitulo(titulo);
                        Livro.validarAutor(autor);
                        Livro.validarAno(ano);

                        arvore.inserir(new Livro(chave, titulo, autor, ano));
                        System.out.println("Livro inserido com sucesso!");

                    } catch (ModelException e) {
                        System.out.println("Erro: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Entrada inválida. Tente novamente.");
                        scanner.nextLine();
                    }
                    break;

                case 2:
                    try {
                        System.out.print("Digite a chave do livro a remover: ");
                        int chaveRemover = scanner.nextInt();
                        arvore.remover(chaveRemover);
                        System.out.println("Livro removido com sucesso!");
                    } catch (ModelException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        System.out.print("Digite a chave do livro para busca: ");
                        int chaveBuscar = scanner.nextInt();
                        scanner.nextLine();

                        Livro encontrado = arvore.buscar(chaveBuscar);

                        if (encontrado != null) {
                            System.out.println("Busca: " + encontrado.getChave() + " - " +
                                               encontrado.getTitulo() + " (" +
                                               encontrado.getAutor() + ", " +
                                               encontrado.getAno() + ")");
                        } else {
                            System.out.println("Livro não encontrado.");
                        }
                    } catch (ModelException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("\n--- Em Ordem ---");
                    if (arvore.getTamanho() == 0) {
                        System.out.println("A biblioteca está vazia.");
                    } else {
                        for (Livro l : arvore.emOrdem()) {
                            System.out.println(l.getChave() + " - " + l.getTitulo() + 
                                               " (" + l.getAutor() + ", " + l.getAno() + ")");
                        }
                    }
                    break;

                case 5:
                    System.out.println("Tamanho atual da árvore: " + arvore.getTamanho());
                    break;

                case 0:
                    System.out.println("Encerrando o sistema...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 0);

        scanner.close();
    }
}
