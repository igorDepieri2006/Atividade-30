package br.com.starlog.model;

public class Produto {
    private Long id;
    private String nome;
    private double preco;
    private int estoque;

    // Construtor de Persistência (com ID - usado ao hidratar tuplas do banco)
    public Produto(Long id, String nome, double preco, int estoque) {
        validarInvariantes(nome, preco, estoque);
        this.id = id;
        this.nome = nome.trim();
        this.preco = preco;
        this.estoque = estoque;
    }

    // Construtor de Negócio (sem ID - usado para novas instâncias antes de salvar)
    public Produto(String nome, double preco, int estoque) {
        this(null, nome, preco, estoque);
    }

    // Controle de Invariantes de Domínio
    private void validarInvariantes(String nome, double preco, int estoque) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome do produto não pode ser nulo e deve ter pelo menos 3 caracteres.");
        }
        if (preco <= 0.0) {
            throw new IllegalArgumentException("O preço deve ser estritamente positivo (maior que 0.0).");
        }
        if (estoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("Nome inválido.");
        }
        this.nome = nome.trim();
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco <= 0.0) {
            throw new IllegalArgumentException("Preço deve ser positivo.");
        }
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo.");
        }
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return String.format("ID: %-3d | Nome: %-25s | Preço: R$ %8.2f | Estoque: %3d un",
                id != null ? id : 0, nome, preco, estoque);
    }
}