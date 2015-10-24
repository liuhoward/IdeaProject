package textlib;

import datalib.SparseInstances;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by howard on 10/24/15.
 */
public class TextData {

    public SparseInstances genSparseInstancesSen(JSONArray array) throws Exception{
        if(array == null){
            System.out.println("error: array is null!\n");
            return null;
        }

        TextUtil textUtil = new TextUtil();
        textUtil.importDict(null);
        textUtil.importFilterWords(null);
        Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
        int size = array.size();
        int attDefault = 32768;
        SparseInstances instData = new SparseInstances(attDefault, size * 5);

        String idkey = "id";
        String reviewKey = "review";
        for (JSONObject jsonObj : (List<JSONObject>) array){
            long id = (long)jsonObj.get(idkey);
            String review = (String)jsonObj.get(reviewKey);
            if(review == null){
                continue;
            }
            int missCount = 0;
            int sumCount = 0;
            String[] sentences =
                    textUtil.sentenceDetect(review.toLowerCase().replaceAll("[^\\x20-\\x7E]", " "));
            if(sentences == null){
                continue;
            }
            StringBuffer sb = new StringBuffer(256);
            String term = null;
            for(int j = 0; j < sentences.length; j++){
                String[] tokens = textUtil.tokenize(sentences[j].replaceAll("_|-", " "));
                if(tokens == null){
                    continue;
                }
                sumCount += tokens.length;
                for(int k = 0; k < tokens.length; k++){
                    if(tokens[k].isEmpty()){
                        continue;
                    }
                    term = stemWord(tokens[k].replaceAll("[^A-Za-z0-9]", ""));
                    if(term == null || term.isEmpty()){
                        missCount++;
                        term = (String)stemmer.stem(tokens[k]);
                    }
                    if(term.matches("^[.,!?:;]") && sb.length() > 1){
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sb.append(term);
                    sb.append(" ");
                }
            }




        }
    }

    public SparseInstances genSparseInstancesReview(JSONArray array) {




    }



}
