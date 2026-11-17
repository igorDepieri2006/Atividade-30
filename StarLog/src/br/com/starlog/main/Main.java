package br.com.starlog.main;

import br.com.starlog.dao.ProdutoDAO;
import br.com.starlog.dao.ProdutoDAOSqlite;
import br.com.starlog.model.Produto;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("🚀 STARLOG ALMOXARIFADO • CICLO DE VIDA CRUD (SQLite)");
        System.out.println("==================================================================");

        // Inversão de Dependência (DIP)
        ProdutoDAO dao = new ProdutoDAOSqlite();

        // 1. [Create]: Cadastrar dois itens e mostrar os IDs gerados
        System.out.println("\n--- 1. [CREATE] CADASTRO DE PRODUTOS ---");
        Produto p1 = new Produto("Teclado Mecânico RGB", 250.00, 10);
        Produto p2 = new Produto("Mousepad Speed XL", 75.50, 25);

        dao.salvar(p1);
        dao.salvar(p2);

        System.out.println("✅ Produtos persistidos com sucesso!");
        System.out.println("   -> " + p1);
        System.out.println("   -> " + p2);

        // 2. [Read]: Listar todos os produtos com exibição limpa em colunas
        System.out.println("\n--- 2. [READ] LISTAGEM INICIAL DE ESTOQUE ---");
        imprimirTabela(dao.listarTodos());

        // 3. [Update]: Alterar o estoque do Teclado para 15 e preço para 289.90
        System.out.println("\n--- 3. [UPDATE] ATUALIZANDO DADOS DO TECLADO ---");
        p1.setEstoque(15);
        p1.setPreco(289.90);
        
        boolean atualizado = dao.atualizar(p1);
        System.out.println("Operação de Update bem-sucedida? " + (atualizado ? "SIM (true)" : "NÃO (false)"));
        
        // Exibir registro atualizado buscando do banco
        dao.buscarPorId(p1.getId()).ifPresent(prod -> 
            System.out.println("   -> Registro Atualizado no Banco: " + prod)
        );

        // 4. [Delete]: Remover o Mousepad por ID e testar ID inexistente (888L)
        System.out.println("\n--- 4. [DELETE] REMOÇÃO DE ITENS ---");
        boolean deleteMousepad = dao.excluir(p2.getId());
        System.out.println("Remoção do Mousepad (ID " + p2.getId() + "): " + (deleteMousepad ? "Sucesso (true)" : "Falha"));

        boolean deleteInexistente = dao.excluir(888L);
        System.out.println("Tentativa de remover ID inexistente (888L): " + (deleteInexistente ? "Sucesso" : "Seguro / Retornou false (sem travamentos)"));

        // 5. [Final State]: Executar listarTodos() comprovando que restou exatamente 1 produto
        System.out.println("\n--- 5. [FINAL STATE] ESTADO ATUAL DO BANCO ---");
        List<Produto> estoqueFinal = dao.listarTodos();
        imprimirTabela(estoqueFinal);
        System.out.println("📌 Total exato de produtos restantes no almoxarifado: " + estoqueFinal.size());
    }

    private static void imprimirTabela(List<Produto> produtos) {
        System.out.println("==================================================================================");
        System.out.println(String.format("%-5s | %-25s | %-15s | %s", "ID", "NOME", "PREÇO", "ESTOQUE"));
        System.out.println("==================================================================================");
        for (Produto p : produtos) {
            System.out.println(String.format("%-5d | %-25s | R$ %10.2f | %3d un",
                    p.getId(), p.getNome(), p.getPreco(), p.getEstoque()));
        }
        System.out.println("==================================================================================");
    }
}