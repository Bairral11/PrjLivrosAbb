# Biblioteca Virtual com Árvore Binária de Busca (ABB)

Sistema de cadastro e busca de livros desenvolvido em **Java** para a disciplina de **Estruturas de Dados**. Os livros ficam armazenados em uma **Árvore Binária de Busca**, organizados pelo código identificador (chave), o que permite consultas mais eficientes do que uma busca sequencial em uma lista desordenada.

O sistema pode ser usado de duas formas: pelo **console** (menu de texto) ou por uma **interface gráfica** feita com Swing e WindowBuilder.

## Funcionalidades

- **Inserir** um livro (chave, título, autor e ano de publicação)
- **Buscar** um livro pela chave
- **Remover** um livro pela chave
- **Listar** todos os livros em ordem crescente de chave (percurso em-ordem)
- **Consultar o tamanho** da árvore (quantidade de livros cadastrados)

## Como a ABB funciona

Cada nó da árvore guarda um livro e duas referências, uma para o filho esquerdo e outra para o filho direito. A posição do livro é definida pela comparação das chaves:

- chaves **menores** ficam na subárvore **esquerda**;
- chaves **maiores** ficam na subárvore **direita**;
- chaves **repetidas** não são aceitas.

```
        50
       /  \
     30    70
    /  \  /  \
   20  40 60  80
```

| Operação | Como é feita |
| --- | --- |
| Inserção | Compara a chave com o nó atual e desce para a esquerda ou para a direita até achar uma posição vazia. |
| Busca | Mesma lógica da inserção. Para ao encontrar a chave ou ao chegar a um nó nulo. |
| Listagem | Percurso em-ordem: esquerda → nó → direita, o que gera as chaves em ordem crescente. |
| Remoção | Se o nó tem 0 ou 1 filho, ele é substituído pelo filho. Se tem 2 filhos, recebe o livro de menor chave da subárvore direita, e esse livro é removido da posição original. |

> **Limitação:** a ABB não se autoequilibra. Se as chaves forem inseridas já ordenadas (1, 2, 3, 4...), a árvore degenera em uma lista e as operações passam de O(log n) para O(n) no pior caso. Uma AVL resolveria isso.

## Estrutura do projeto

```
src/
├── controller/
│   └── CtrlPrograma.java    # Menu em console
├── model/
│   ├── Livro.java           # Entidade com as validações dos dados
│   └── ModelException.java  # Exceção para erros de regra de negócio
├── util/
│   ├── ABB.java             # Árvore Binária de Busca
│   └── No.java              # Nó da árvore (livro + filhos esquerdo e direito)
└── view/
    └── TelaPrincipal.java   # Interface gráfica (Swing / WindowBuilder)
```

## Regras de validação

As validações ficam na classe `Livro` e lançam `ModelException` com uma mensagem clara:

| Campo | Regra |
| --- | --- |
| Chave | Deve ser maior que 0 |
| Título | Não pode ser nulo nem vazio |
| Autor | Não pode ser nulo nem vazio |
| Ano | Deve ser maior que 0 |

Além disso, a `ABB` lança `ModelException` ao inserir uma chave já cadastrada e ao remover uma chave que não existe.

## Exemplo de uso

1. Insira os livros com as chaves `50`, `30`, `70`, `20` e `40`.
2. Use **Listar** e veja os livros em ordem: 20, 30, 40, 50, 70.
3. Busque a chave `40` para ver os dados do livro.
4. Remova a chave `30` (nó com dois filhos). A árvore se reorganiza e a listagem continua em ordem.

## Integrantes

Fernando, João Vitor, João Pedro, Lucas e Manuella.
