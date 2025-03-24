import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;

public class MongoCRUD {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoCRUD() {
        // 1. CONEXÃO
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        System.out.println("Conectado ao MongoDB");

        // 2. CREATE DATABASE (se não existir)
        this.database = mongoClient.getDatabase("lab_mongodb");
        System.out.println("Banco de dados 'lab_mongodb' selecionado/criado");

        // 3. CREATE COLLECTION (se não existir)
        this.collection = database.getCollection("pessoas");
        System.out.println("Coleção 'pessoas' selecionada/criada");
    }

    // CREATE DOCUMENTS
    public void createDocuments() {
        System.out.println("\n=== CREATE DOCUMENTS ===");

        // Inserir um documento
        Document pessoa1 = new Document("nome", "João Silva")
                .append("idade", 28)
                .append("cidade", "São Paulo")
                .append("profissao", "Engenheiro")
                .append("salario", 8500.00);
        collection.insertOne(pessoa1);
        System.out.println("Documento 1 inserido: " + pessoa1);

        // Inserir múltiplos documentos
        Document pessoa2 = new Document("nome", "Maria Oliveira")
                .append("idade", 32)
                .append("cidade", "Rio de Janeiro")
                .append("profissao", "Médica")
                .append("salario", 12000.00);

        Document pessoa3 = new Document("nome", "Carlos Souza")
                .append("idade", 25)
                .append("cidade", "Belo Horizonte")
                .append("profissao", "Desenvolvedor")
                .append("salario", 5500.00);

        collection.insertMany(Arrays.asList(pessoa2, pessoa3));
        System.out.println("Documentos 2 e 3 inseridos");
    }

    // READ (FIND)
    public void readDocuments() {
        System.out.println("\n=== READ DOCUMENTS ===");

        // Encontrar todos
        System.out.println("Todos os documentos:");
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }

        // Encontrar com filtro
        System.out.println("\nPessoas com salário > 6000:");
        Bson filter = Filters.gt("salario", 6000);
        for (Document doc : collection.find(filter)) {
            System.out.println(doc.toJson());
        }
    }

    // UPDATE
    public void updateDocuments() {
        System.out.println("\n=== UPDATE DOCUMENTS ===");

        // Atualizar um documento
        Bson filter = Filters.eq("nome", "João Silva");
        Bson update = Updates.set("salario", 9000.00);
        UpdateResult result = collection.updateOne(filter, update);
        System.out.println("Documentos modificados: " + result.getModifiedCount());

        // Atualizar múltiplos documentos
        Bson filterAll = Filters.gt("idade", 25);
        Bson updateAll = Updates.inc("salario", 500.00);
        UpdateResult resultAll = collection.updateMany(filterAll, updateAll);
        System.out.println("Documentos modificados (aumento salarial): " + resultAll.getModifiedCount());
    }

    // DELETE
    public void deleteDocuments() {
        System.out.println("\n=== DELETE DOCUMENTS ===");

        // Deletar um documento
        Bson filter = Filters.eq("nome", "Carlos Souza");
        DeleteResult result = collection.deleteOne(filter);
        System.out.println("Documentos deletados: " + result.getDeletedCount());

        // Deletar múltiplos documentos (exemplo condicional)
        Bson filterCondition = Filters.lt("salario", 7000);
        DeleteResult resultMany = collection.deleteMany(filterCondition);
        System.out.println("Documentos deletados (salário < 7000): " + resultMany.getDeletedCount());
    }

    // INDEX
    public void createIndexes() {
        System.out.println("\n=== CREATE INDEXES ===");

        // Criar índice único no campo nome
        collection.createIndex(Indexes.ascending("nome"), new IndexOptions().unique(true));
        System.out.println("Índice único criado no campo 'nome'");

        // Criar índice composto
        collection.createIndex(Indexes.compoundIndex(
                Indexes.ascending("profissao"),
                Indexes.descending("salario")
        ));
        System.out.println("Índice composto criado em 'profissao' e 'salario'");

        // Listar índices
        System.out.println("\nÍndices existentes:");
        for (Document index : collection.listIndexes()) {
            System.out.println(index.toJson());
        }
    }

    // AGGREGATION
    public void aggregationExamples() {
        System.out.println("\n=== AGGREGATION EXAMPLES ===");

        // 1. Média de salário por profissão
        System.out.println("\nMédia salarial por profissão:");
        collection.aggregate(Arrays.asList(
                Aggregates.group("$profissao",
                        Accumulators.avg("mediaSalarial", "$salario"),
                        Accumulators.sum("totalSalarios", "$salario"),
                        Accumulators.first("exemploNome", "$nome")
                )
        )).forEach(doc -> System.out.println(doc.toJson()));

        // 2. Pessoas agrupadas por cidade com contagem
        System.out.println("\nContagem de pessoas por cidade:");
        collection.aggregate(Arrays.asList(
                Aggregates.group("$cidade",
                        Accumulators.sum("quantidade", 1)
                ),
                Aggregates.sort(Sorts.descending("quantidade"))
        )).forEach(doc -> System.out.println(doc.toJson()));
    }

    public void close() {
        mongoClient.close();
        System.out.println("\nConexão com MongoDB encerrada");
    }

    public static void main(String[] args) {
        MongoCRUD mongoLab = new MongoCRUD();

        try {
            // Operações CRUD
            mongoLab.createDocuments();
            mongoLab.readDocuments();
            mongoLab.updateDocuments();
            mongoLab.readDocuments(); // Verificar updates
            mongoLab.deleteDocuments();
            mongoLab.readDocuments(); // Verificar deletes

            // Operações avançadas
            mongoLab.createIndexes();
            mongoLab.aggregationExamples();
        } finally {
            mongoLab.close();
        }
    }
}