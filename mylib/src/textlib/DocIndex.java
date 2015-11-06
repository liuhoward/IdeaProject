package textlib;

import datalib.ESparseInstance;
import datalib.SparseInstances;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.soap.Text;
import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
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

        Analyzer analyzer = null;
        if(stopwordsFile == null){
            analyzer = new SimpleAnalyzer();
        }else {
            analyzer = new StopAnalyzer(Paths.get(stopwordsFile));
        }

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), iwc);
        indexWriter.deleteAll();

        TextUtil textUtil = new TextUtil();

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

            String[] tokens = textUtil.tokenize(review);
            if(tokens.length <= TERM_MIN_THRESHOLD){
                continue;
            }

            String[] sentences = textUtil.sentenceDetect(review);
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

        Analyzer analyzer = null;
        if(stopwordsFile == null){
            analyzer = new SimpleAnalyzer();
        }else {
            analyzer = new StopAnalyzer(Paths.get(stopwordsFile));
        }

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), iwc);
        indexWriter.deleteAll();

        TextUtil textUtil = new TextUtil();

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

            String[] tokens = textUtil.tokenize(review);
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

        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        Terms terms = SlowCompositeReaderWrapper.wrap(reader).terms(reviewKey);

        int capacity = (int)terms.size();
        HashMap<String, Integer> wordDict = new HashMap<>(capacity);
        capacity = capacity > 65535 ? 65535:capacity;
        SparseInstances instData = new SparseInstances(capacity, reader.numDocs());
        TermsEnum termsEnum = terms.iterator();
        int index = 0;
        BytesRef term = null;
        String strTerm = null;
        while((term = termsEnum.next()) != null){
            strTerm = term.toString();
            if(termsEnum.totalTermFreq() < threshold){
                continue;
            }
            if(strTerm.isEmpty()){
                continue;
            }
            if(wordDict.get(strTerm) != null){
                continue;
            }
            instData.addAttribute(strTerm);
            index++;
        }
        int numAtt = instData.numAttributes();
        int numInst = instData.numInstances();
        Integer attIndex = null;
        String id = null;
        int termIndex = 0;
        for(int docIndex = 0; docIndex < numInst; docIndex++){
            id = reader.document(docIndex).getField(idKey).stringValue();
            Terms docTerms = reader.getTermVector(docIndex, reviewKey);
            if(docTerms == null){
                continue;
            }
            int[] indices = new int[(int)docTerms.size()];
            double[] attValues = new double[(int)docTerms.size()];
            termsEnum = docTerms.iterator();
            termIndex = 0;
            while((term = termsEnum.next()) != null){
                strTerm = term.toString();
                attIndex = wordDict.get(strTerm);
                if(attIndex == null){
                    continue;
                }
                indices[termIndex] = attIndex.intValue();
                attValues[termIndex] = termsEnum.totalTermFreq();
            }
            ESparseInstance instance = new ESparseInstance(id, 1.0, attValues, indices, numAtt);
            instData.addInstance(instance);
        }

        return null;


    }

}
