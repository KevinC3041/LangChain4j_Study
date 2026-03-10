package com.cx.consultant.rag;

//import com.cx.consultant.factory.RedisEmbeddingStoreFactory;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
//@Component
public class RagIngestRunner implements ApplicationRunner {

    private static final int BATCH_SIZE = 10;

    private final EmbeddingStore embeddingStore;
    private final EmbeddingModel embeddingModel;

//    private final RedisEmbeddingStoreFactory storeFactory;

    public RagIngestRunner(
//            @Qualifier("myEmbeddingStore") EmbeddingStore embeddingStore,
            @Qualifier("yangmingStore") RedisEmbeddingStore redisEmbeddingStore,
//            RedisEmbeddingStoreFactory storeFactory,
            EmbeddingModel embeddingModel) {
//        this.embeddingStore = embeddingStore;
        this.embeddingStore = redisEmbeddingStore;
//        this.storeFactory = storeFactory;
//        this.embeddingStore = storeFactory.create("cybergyangming");
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

//        List<Document> documents = new ArrayList<>();
//
////        pdf
//        documents.addAll(
//                ClassPathDocumentLoader.loadDocuments(
//                        "content/pdfs",
//                        new ApachePdfBoxDocumentParser()
//                )
//        );
//
////        txt/md
//        documents.addAll(
//                ClassPathDocumentLoader.loadDocuments(
//                        "content/texts",
//                        new TextDocumentParser()
//                )
//        );

//        构建文档分割器对象
        DocumentSplitter ds = DocumentSplitters.recursive(500, 100);


//        // 3.手动分批embedding（核心），控制 embedding 请求的批处理大小，因为国产api限制大小为10。实际可在yml中配置max-segments-per-batch
//        List<TextSegment> segments = documents.stream()
//                .flatMap(doc -> ds.split(doc).stream())
//                .toList();
//
//        for (int i=0; i < segments.size(); i += BATCH_SIZE) {
//            List<TextSegment> batch = segments.subList(
//                    i,
//                    Math.min(segments.size(), i + BATCH_SIZE)
//            );
//
//            var embeddings = embeddingModel.embedAll(batch).content();
//            embeddingStore.addAll(embeddings, batch);
//        }
//
//        log.info("RAG ingest finished, total segments: " + segments.size());


////      //  3.构建一个EmbeddingStoreIngestor对象
//        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(embeddingModel)
//                .documentSplitter(ds)
//                .build();

        // 加载文档
        List<Document> documents = loadDocuments();

        // 3的工作-分割文本
        List<TextSegment> allSegments = new ArrayList<>();
        for (Document doc : documents) {
            allSegments.addAll(ds.split(doc));
        }
        log.info("分割为 {} 个文本片段", allSegments.size());

        // 3的工作-生成Embeddings（这里可以用较大批次，因为只是调用API）
        var embeddings = embeddingModel.embedAll(allSegments).content();
        log.info("生成 {} 个embeddings完成", embeddings.size());

        // 3的工作-分别存储到Redis，避免一次性太多条导致超时
        int STORE_BATCH_SIZE = 200;
        for (int i = 0; i < allSegments.size(); i += STORE_BATCH_SIZE) {
            int end = Math.min(i + STORE_BATCH_SIZE, allSegments.size());

            List<TextSegment> segmentBatch = allSegments.subList(i, end);
            List<Embedding> embeddingBatch = embeddings.subList(i, end);

            // 3的工作-分批allAll，每次最多200条
            embeddingStore.addAll(embeddingBatch, segmentBatch);

            log.info("已存储 {}/{}", end, allSegments.size());
        }

        log.info("RAG ingest finished, total segments: {}", allSegments.size());


//        ingestor.ingest(documents);
//        System.out.println("RAG ingest finished.");
    }

    private List<Document> loadDocuments() {
        List<Document> documents = new ArrayList<>();

//        pdf
        documents.addAll(
                ClassPathDocumentLoader.loadDocuments(
                        "content/pdfs",
                        new ApachePdfBoxDocumentParser()
                )
        );

////        txt/md
//        documents.addAll(
//                ClassPathDocumentLoader.loadDocuments(
//                        "content/texts",
//                        new TextDocumentParser()
//                )
//        );

        return documents;
    }

}
