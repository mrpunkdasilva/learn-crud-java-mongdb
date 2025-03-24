# Learn CRUD Java With MongoDB

## Introdução ao MongoDB com Java

Bem-vindo a este tutorial prático onde você aprenderá a realizar operações CRUD (Create, Read, Update, Delete) no MongoDB usando o driver Java. Ao final, você será capaz de construir aplicações Java que interagem com bancos de dados MongoDB.

### Pré-requisitos
- Conhecimento básico de Java
- MongoDB instalado localmente (porta padrão 27017)
- Java Development Kit (JDK) 8 ou superior
- Maven ou Gradle para gerenciamento de dependências

## Passo 1: Configurar Seu Projeto

Primeiro, adicione o driver Java do MongoDB ao seu projeto:

**Maven:**
```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.9.0</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'org.mongodb:mongodb-driver-sync:4.9.0'
```

## Passo 2: Conectar ao MongoDB

Crie uma nova classe Java `MongoCRUD.java` e adicione o código de conexão:

```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoCRUD {
    private MongoClient mongoClient;
    
    public MongoCRUD() {
        // Conectar ao MongoDB rodando localmente
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        System.out.println("Conectado ao MongoDB com sucesso!");
    }
    
    public void close() {
        mongoClient.close();
        System.out.println("Conexão encerrada.");
    }
    
    public static void main(String[] args) {
        MongoCRUD mongoApp = new MongoCRUD();
        try {
            // Nossas operações virão aqui
        } finally {
            mongoApp.close();
        }
    }
}
```

## Passo 3: Criar Documentos

Vamos adicionar um método para inserir dados na coleção "pessoas":

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Arrays;

public class MongoCRUD {
    // ... código anterior ...
    
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    
    public MongoCRUD() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.database = mongoClient.getDatabase("lab_mongodb");
        this.collection = database.getCollection("pessoas");
    }
    
    public void criarDocumentos() {
        System.out.println("\n=== Criando Documentos ===");
        
        // Inserção de documento único
        Document pessoa1 = new Document("nome", "João Silva")
                .append("idade", 28)
                .append("cidade", "São Paulo");
        collection.insertOne(pessoa1);
        System.out.println("Documento 1 inserido: " + pessoa1);
        
        // Inserção de múltiplos documentos
        Document pessoa2 = new Document("nome", "Maria Oliveira")
                .append("idade", 32)
                .append("cidade", "Rio de Janeiro");
                
        Document pessoa3 = new Document("nome", "Carlos Souza")
                .append("idade", 25)
                .append("cidade", "Belo Horizonte");
                
        collection.insertMany(Arrays.asList(pessoa2, pessoa3));
        System.out.println("Mais 2 documentos inseridos");
    }
    
    public static void main(String[] args) {
        MongoCRUD mongoApp = new MongoCRUD();
        try {
            mongoApp.criarDocumentos();
        } finally {
            mongoApp.close();
        }
    }
}
```

## Passo 4: Ler Documentos

Adicione capacidades de consulta à sua aplicação:

```java
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.conversions.Bson;

public class MongoCRUD {
    // ... código anterior ...
    
    public void lerDocumentos() {
        System.out.println("\n=== Lendo Documentos ===");
        
        // 1. Encontrar todos os documentos
        System.out.println("Todos os documentos:");
        collection.find().forEach(doc -> System.out.println(doc.toJson()));
        
        // 2. Encontrar com filtro (idade > 25)
        System.out.println("\nPessoas com mais de 25 anos:");
        Bson filtroIdade = Filters.gt("idade", 25);
        collection.find(filtroIdade)
                 .forEach(doc -> System.out.println(doc.toJson()));
        
        // 3. Projeção (apenas nome e cidade)
        System.out.println("\nApenas nomes e cidades:");
        Bson projecao = Projections.fields(
            Projections.include("nome", "cidade"),
            Projections.excludeId()
        );
        collection.find()
                 .projection(projecao)
                 .forEach(doc -> System.out.println(doc.toJson()));
    }
    
    public static void main(String[] args) {
        MongoCRUD mongoApp = new MongoCRUD();
        try {
            mongoApp.criarDocumentos();
            mongoApp.lerDocumentos();
        } finally {
            mongoApp.close();
        }
    }
}
```

## Passo 5: Atualizar Documentos

Implemente atualizações de documentos:

```java
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

public class MongoCRUD {
    // ... código anterior ...
    
    public void atualizarDocumentos() {
        System.out.println("\n=== Atualizando Documentos ===");
        
        // 1. Atualizar um documento
        Bson filtro = Filters.eq("nome", "João Silva");
        Bson atualizacao = Updates.set("profissao", "Engenheiro");
        UpdateResult resultado = collection.updateOne(filtro, atualizacao);
        System.out.println(resultado.getModifiedCount() + " documento(s) modificado(s)");
        
        // 2. Atualizar múltiplos documentos
        Bson filtroIdade = Filters.gt("idade", 25);
        Bson atualizacaoSalario = Updates.inc("salario", 500.00);
        UpdateResult resultadoMultiplo = collection.updateMany(filtroIdade, atualizacaoSalario);
        System.out.println(resultadoMultiplo.getModifiedCount() + " documentos atualizados com aumento salarial");
        
        // 3. Atualização complexa (definir se não existir)
        Bson atualizacaoComplexa = Updates.combine(
            Updates.set("profissao", "Desenvolvedor"),
            Updates.setOnInsert("data_criacao", new java.util.Date())
        );
        collection.updateOne(Filters.eq("nome", "Carlos Souza"), atualizacaoComplexa);
    }
    
    public static void main(String[] args) {
        MongoCRUD mongoApp = new MongoCRUD();
        try {
            mongoApp.criarDocumentos();
            mongoApp.lerDocumentos();
            mongoApp.atualizarDocumentos();
            mongoApp.lerDocumentos(); // Verificar atualizações
        } finally {
            mongoApp.close();
        }
    }
}
```

## Passo 6: Deletar Documentos

Adicione métodos de exclusão:

```java
import com.mongodb.client.result.DeleteResult;

public class MongoCRUD {
    // ... código anterior ...
    
    public void deletarDocumentos() {
        System.out.println("\n=== Deletando Documentos ===");
        
        // 1. Deletar um documento
        Bson filtro = Filters.eq("nome", "Carlos Souza");
        DeleteResult resultado = collection.deleteOne(filtro);
        System.out.println(resultado.getDeletedCount() + " documento(s) deletado(s)");
        
        // 2. Deletar múltiplos documentos
        Bson filtroIdade = Filters.lt("idade", 30);
        DeleteResult resultadoMultiplo = collection.deleteMany(filtroIdade);
        System.out.println(resultadoMultiplo.getDeletedCount() + " documentos deletados onde idade < 30");
    }
    
    public static void main(String[] args) {
        MongoCRUD mongoApp = new MongoCRUD();
        try {
            mongoApp.criarDocumentos();
            mongoApp.lerDocumentos();
            mongoApp.atualizarDocumentos();
            mongoApp.deletarDocumentos();
            mongoApp.lerDocumentos(); // Verificar exclusões
        } finally {
            mongoApp.close();
        }
    }
}
```

## Passo 7: Recursos Avançados

### Indexação

```java
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;

public class MongoCRUD {
    // ... código anterior ...
    
    public void gerenciarIndices() {
        System.out.println("\n=== Gerenciando Índices ===");
        
        // 1. Criar índice único no nome
        collection.createIndex(
            Indexes.ascending("nome"),
            new IndexOptions().unique(true)
        );
        System.out.println("Índice único criado em 'nome'");
        
        // 2. Criar índice composto
        collection.createIndex(
            Indexes.compoundIndex(
                Indexes.ascending("cidade"),
                Indexes.descending("idade")
            )
        );
        System.out.println("Índice composto criado em cidade e idade");
        
        // 3. Listar todos os índices
        System.out.println("\nÍndices atuais:");
        collection.listIndexes().forEach(idx -> System.out.println(idx.toJson()));
    }
}
```

### Agregação

```java
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;

public class MongoCRUD {
    // ... código anterior ...
    
    public void exemplosAgregacao() {
        System.out.println("\n=== Exemplos de Agregação ===");
        
        // 1. Média de idade por cidade
        System.out.println("\nMédia de idade por cidade:");
        collection.aggregate(Arrays.asList(
            Aggregates.group("$cidade", 
                Accumulators.avg("mediaIdade", "$idade"),
                Accumulators.sum("contagem", 1)
            ),
            Aggregates.sort(Sorts.ascending("_id"))
        ).forEach(doc -> System.out.println(doc.toJson()));
        
        // 2. Estatísticas salariais por profissão
        System.out.println("\nEstatísticas salariais por profissão:");
        collection.aggregate(Arrays.asList(
            Aggregates.group("$profissao",
                Accumulators.avg("salarioMedio", "$salario"),
                Accumulators.min("salarioMinimo", "$salario"),
                Accumulators.max("salarioMaximo", "$salario")
            ),
            Aggregates.sort(Sorts.descending("salarioMedio"))
        )).forEach(doc -> System.out.println(doc.toJson()));
    }
}
```

## Exemplo Completo

Aqui está a classe completa com todas as operações:

```java
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.Arrays;

public class MongoCRUD {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    
    public MongoCRUD() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.database = mongoClient.getDatabase("lab_mongodb");
        this.collection = database.getCollection("pessoas");
        System.out.println("Conexão com MongoDB estabelecida");
    }
    
    // Todos os métodos que criamos acima...
    public void criarDocumentos() { /* ... */ }
    public void lerDocumentos() { /* ... */ }
    public void atualizarDocumentos() { /* ... */ }
    public void deletarDocumentos() { /* ... */ }
    public void gerenciarIndices() { /* ... */ }
    public void exemplosAgregacao() { /* ... */ }
    
    public void close() {
        mongoClient.close();
        System.out.println("Conexão encerrada");
    }
    
    public static void main(String[] args) {
        MongoCRUD mongoApp = new MongoCRUD();
        try {
            mongoApp.criarDocumentos();
            mongoApp.lerDocumentos();
            mongoApp.atualizarDocumentos();
            mongoApp.lerDocumentos();
            mongoApp.deletarDocumentos();
            mongoApp.lerDocumentos();
            mongoApp.gerenciarIndices();
            mongoApp.exemplosAgregacao();
        } finally {
            mongoApp.close();
        }
    }
}
```

## Boas Práticas

1. **Gerenciamento de Conexão**: Sempre feche seu MongoClient quando terminar
2. **Operações em Massa**: Para múltiplas escritas, use operações em massa
3. **Projeções**: Recupere apenas os campos que você precisa
4. **Tratamento de Erros**: Adicione blocos try-catch adequados em código de produção
5. **Indexação**: Crie índices para seus padrões de consulta comuns
