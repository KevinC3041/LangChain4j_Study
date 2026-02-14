package com.cx.consultant.rag;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
//@RequiredArgsConstructor
public class RagIngestRunner implements ApplicationRunner {

    private static final int BATCH_SIZE = 10;

    private final EmbeddingStore embeddingStore;
    private final EmbeddingModel embeddingModel;

    public RagIngestRunner(
//            @Qualifier("myEmbeddingStore") EmbeddingStore embeddingStore,
            RedisEmbeddingStore redisEmbeddingStore,
            EmbeddingModel embeddingModel) {
//        this.embeddingStore = embeddingStore;
        this.embeddingStore = redisEmbeddingStore;
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


//      //  3.构建一个EmbeddingStoreIngestor对象
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(ds)
                .build();

        // 加载文档
        List<Document> documents = loadDocuments();

        ingestor.ingest(documents);
        System.out.println("RAG ingest finished.");
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

//        txt/md
        documents.addAll(
                ClassPathDocumentLoader.loadDocuments(
                        "content/texts",
                        new TextDocumentParser()
                )
        );

        return documents;
    }
}
