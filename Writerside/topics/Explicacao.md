# Como Tudo Funciona

Este tutorial explica passo a passo como realizar operações CRUD (Create, Read, Update, Delete) no MongoDB usando Java, desde a conexão até consultas avançadas com agregação.

## **1. Conexão com o MongoDB**

### **Como Funciona?** {id="como-funciona_1"}
O MongoDB usa o MongoClient para gerenciar conexões. Quando você cria uma instância dele, ele abre um pool de conexões (conexões reutilizáveis para melhor desempenho).

### **Código Explicado** {id="c-digo-explicado_1"}
```java
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
MongoDatabase database = mongoClient.getDatabase("lab_mongodb");
MongoCollection<Document> collection = database.getCollection("pessoas");
```

- **MongoClients.create()** - Conecta ao servidor MongoDB (rodando em localhost:27017)
- **getDatabase()** - Pega (ou cria, se não existir) o banco de dados lab_mongodb
- **getCollection()** - Acessa (ou cria) a coleção pessoas

Importante: Sempre feche a conexão com mongoClient.close() para evitar vazamentos.

## **2. Inserindo Dados (Create)**

### **Como Funciona?** {id="como-funciona_2"}
- insertOne() - Insere um único documento (registro)
- insertMany() - Insere vários documentos de uma vez (mais eficiente)

### **Código Explicado** {id="c-digo-explicado_2"}
```java
// Inserindo um documento
Document pessoa = new Document("nome", "João Silva")
    .append("idade", 30)
    .append("cidade", "São Paulo");

collection.insertOne(pessoa);

// Inserindo vários documentos
List<Document> pessoas = Arrays.asList(
    new Document("nome", "Maria").append("idade", 25),
    new Document("nome", "Carlos").append("idade", 40)
);

collection.insertMany(pessoas);
```

- Document - Representa um registro no MongoDB (como um JSON)
- append() - Adiciona campos ao documento

## **3. Consultando Dados (Read)**

### **Como Funciona?** {id="como-funciona_3"}
- find() - Retorna todos os documentos
- find(Filtro) - Retorna apenas documentos que atendem a um critério

### **Filtros Comuns**
| Método | Exemplo | Descrição |
|--------|---------|-----------|
| eq | Filters.eq("nome", "João") | Igual a "João" |
| gt | Filters.gt("idade", 25) | Maior que 25 |
| lt | Filters.lt("idade", 30) | Menor que 30 |
| and | Filters.and(eq, gt) | Combina filtros |

### **Código Explicado** {id="c-digo-explicado_3"}
```java
// Busca todos
collection.find().forEach(doc -> System.out.println(doc.toJson()));

// Busca com filtro (idade > 25)
Bson filtro = Filters.gt("idade", 25);
collection.find(filtro).forEach(doc -> System.out.println(doc));
```

## **4. Atualizando Dados (Update)**

### **Como Funciona?** {id="como-funciona_4"}
- updateOne() - Atualiza o primeiro documento que bate com o filtro
- updateMany() - Atualiza todos os documentos que batem com o filtro

### **Operações de Atualização**
| Método | Exemplo | Descrição |
|--------|---------|-----------|
| set | Updates.set("idade", 31) | Altera o valor |
| inc | Updates.inc("idade", 1) | Incrementa (+1) |
| unset | Updates.unset("cidade") | Remove o campo |

### **Código Explicado**
```java
// Atualiza o salário do João
Bson filtro = Filters.eq("nome", "João Silva");
Bson update = Updates.set("salario", 5000.00);
collection.updateOne(filtro, update);

// Aumenta salário de todos com +500
Bson filtroGeral = Filters.gt("salario", 3000);
Bson updateGeral = Updates.inc("salario", 500);
collection.updateMany(filtroGeral, updateGeral);
```

## **5. Deletando Dados (Delete)**

### **Como Funciona?** {id="como-funciona_5"}
- deleteOne() - Remove o primeiro documento que bate com o filtro
- deleteMany() - Remove todos os documentos que batem com o filtro

### **Código Explicado** {id="c-digo-explicado_4"}
```java
// Deleta o João
collection.deleteOne(Filters.eq("nome", "João Silva"));

// Deleta todos com salário < 3000
collection.deleteMany(Filters.lt("salario", 3000));
```

## **6. Índices (Para Consultas Rápidas)**

### **Como Funciona?** {id="como-funciona_6"}
Índices aceleram buscas, mas ocupam espaço extra.

### **Tipos de Índices**
- Simples - Indexes.ascending("nome")
- Único - new IndexOptions().unique(true)
- Composto - Indexes.compoundIndex(asc, desc)

### **Código Explicado** {id="c-digo-explicado_5"}
```java
// Cria índice único no nome
collection.createIndex(
    Indexes.ascending("nome"),
    new IndexOptions().unique(true)
);

// Cria índice composto (cidade + idade)
collection.createIndex(
    Indexes.compoundIndex(
        Indexes.ascending("cidade"),
        Indexes.descending("idade")
    )
);
```

## **7. Agregações (Análise de Dados)**

### **Como Funciona?**
Agregações permitem filtrar, agrupar e calcular dados em pipelines.

### **Operações Comuns**
| Método | Exemplo | Descrição |
|--------|---------|-----------|
| group | Aggregates.group("$cidade") | Agrupa por cidade |
| avg | Accumulators.avg("media", "$salario") | Calcula média |
| sort | Aggregates.sort(Sorts.descending("idade")) | Ordena resultados |

### **Código Explicado** {id="c-digo-explicado_6"}
```java
// Média salarial por profissão
collection.aggregate(Arrays.asList(
    Aggregates.group("$profissao", 
        Accumulators.avg("mediaSalarial", "$salario")
    ),
    Aggregates.sort(Sorts.descending("mediaSalarial"))
).forEach(doc -> System.out.println(doc));
```

## **Conclusão**
Agora você sabe:
- Como conectar Java ao MongoDB
- Como fazer CRUD (inserir, ler, atualizar, deletar)
- Como acelerar consultas com índices
- Como analisar dados com agregações

Próximos passos:
- Transações (para operações complexas)
- Change Streams (monitorar alterações em tempo real)
- Driver Reativo (para programação assíncrona)

Dica: Experimente modificar os exemplos e criar suas próprias consultas!