package br.com.starlog.dao;

import br.com.starlog.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDAOSqlite implements ProdutoDAO {

    private static final String URL = "jdbc:sqlite:starlog_almoxarifado.db";

    public ProdutoDAOSqlite() {
        criarTabelaSeNaoExistir();
    }

    // DDL Automático
    private void criarTabelaSeNaoExistir() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produtos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco REAL NOT NULL,
                estoque INTEGER NOT NULL
            );
        """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao criar tabela de produtos: " + e.getMessage());
        }
    }

    // Mapeamento Centralizado
    private Produto mapearLinha(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nome = rs.getString("nome");
        double preco = rs.getDouble("preco");
        int estoque = rs.getInt("estoque");
        return new Produto(id, nome, preco, estoque);
    }

    @Override
    public void salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        produto.setId(chaves.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar produto: " + e.getMessage());
        }
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        String sql = "SELECT id, nome, preco, estoque FROM produtos WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinha(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar produto por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        // Ordenação canônica por nome ASC
        String sql = "SELECT id, nome, preco, estoque FROM produtos ORDER BY nome ASC;";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, preco = ?, estoque = ? WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.setLong(4, produto.getId());
            
            int afetadas = stmt.executeUpdate();
            return afetadas > 0; // Garantia determinística de integridade relacional
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int afetadas = stmt.executeUpdate();
            return afetadas > 0; // Retorna true se deletou, false caso contrário
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
            return false;
        }
    }
}
