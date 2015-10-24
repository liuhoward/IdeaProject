package textlib;

import datalib.SparseInstances;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * Created by howard on 10/14/15.
 */
public class DocIndex {

    private String idKey = "id";
    private String reviewKey = "review";
    private int TERM_MIN_THRESHOLD = 10;

    public void createSenIndex(JSONArray jsonArray, String indexPath, String stopwordsFile) throws Exception {

        if (jsonArray == null) {
            System.out.println("error: jsonArray is null!\n");
            return;
        }

        Analyzer analyzer = new StopAnalyzer(Paths.get(stopwordsFile));

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), iwc);
        indexWriter.deleteAll();

        TextStem textStem = new TextStem();

        long startTime = new Date().getTime();

        System.out.println("jsonArray size: " + jsonArray.size());

        long num_sentence = 0;

        for (JSONObject jsonObj : (List<JSONObject>) jsonArray) {
            long id = (long) jsonObj.get(idKey);
            String review = (String) jsonObj.get(reviewKey);

            if(review == null || review.isEmpty()){
                continue;
            }

            if(review.matches(".*[^\\x00-\\x7F].*")){
                continue;
            }

            String[] tokens = textStem.tokenize(review);
            if(tokens.length <= TERM_MIN_THRESHOLD){
                continue;
            }

            String[] sentences = textStem.sentenceDetect(review);
            //System.out.println(body.toLowerCase() + "\n");
            num_sentence = 0;

            for (int i = 0; i < sentences.length; i++) {
                if (sentences[i] == null || sentences[i].isEmpty()) {
                    continue;
                }
                //System.out.println(sentences[i]);
                Document doc = new Document();
                Field idField = new LongField(idKey, id, Field.Store.YES);
                Field numField = new LongField("num", num_sentence, Field.Store.NO);
                Field contentField = new TextField(reviewKey, sentences[i].replaceAll("[_'.,]", " ").replaceAll("[0-9]", ""), Field.Store.YES);

                doc.add(idField);
                doc.add(numField);
                doc.add(contentField);

                indexWriter.addDocument(doc);
                num_sentence++;
            }

        }

        indexWriter.commit();
        indexWriter.close();


        long endTime = new Date().getTime();
        System.out.println("\n\ncreate index time: " + (endTime - startTime) + "ms");
        System.out.println("\n sentence num: " + num_sentence + "\n");

    }


    public void createReviewIndex(JSONArray jsonArray, String indexPath, String stopwordsFile) throws Exception {

        if (jsonArray == null) {
            System.out.println("error: jsonArray is null!\n");
            return;
        }

        Analyzer analyzer = new StopAnalyzer(Paths.get(stopwordsFile));

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), iwc);
        indexWriter.deleteAll();

        TextStem textStem = new TextStem();

        long startTime = new Date().getTime();

        System.out.println("jsonArray size: " + jsonArray.size());

        for (JSONObject jsonObj : (List<JSONObject>) jsonArray) {
            long id = (long) jsonObj.get(idKey);
            String review = (String) jsonObj.get(reviewKey);

            if(review == null || review.isEmpty()){
                continue;
            }

            if(review.matches(".*[^\\x00-\\x7F].*")){
                continue;
            }

            String[] tokens = textStem.tokenize(review);
            if(tokens.length <= TERM_MIN_THRESHOLD){
                continue;
            }

            Document doc = new Document();
            Field idField = new LongField(idKey, id, Field.Store.YES);
            Field contentField = new TextField(reviewKey, review.replaceAll("[_'.,]", " ").replaceAll("[0-9]", ""), Field.Store.YES);

            doc.add(idField);
            doc.add(contentField);

            indexWriter.addDocument(doc);


        }

        indexWriter.commit();
        indexWriter.close();


        long endTime = new Date().getTime();
        System.out.println("\n\ncreate index time: " + (endTime - startTime) + "ms");

    }


    private boolean notEmpty(File indexPath) {

        if (indexPath.isDirectory() == false) {
            System.out.println(indexPath.toString() + " is not directory!\n");
            return false;
        }

        File[] indexs = indexPath.listFiles();

        if (indexs == null) {

            return false;

        }

        return true;
    }

    public SparseInstances readIndex(String indexPath, String destFile, int threshold) throws Exception{

        if(indexPath == null || destFile == null){
            System.out.println("error: indexPath or destFile is null\n");
            return null;
        }

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        Fields fields = MultiFields.getFields(reader);

        return null;


    }

}
