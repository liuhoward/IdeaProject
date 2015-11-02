package textlib;

import datalib.SparseInstances;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
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

        int mapCap = 65536;
        HashMap<String, Long> wordsMap = new HashMap<>(mapCap);

        TextUtil textUtil = new TextUtil();
        SimpleStem simpleStem = new SimpleStem(textUtil);
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
                for(int k = 0; k < tokens.length; k++){
                    if(tokens[k].isEmpty()){
                        continue;
                    }
                    if(textUtil.searchFilterWord(tokens[k])){
                        continue;
                    }
                    term = simpleStem.stem(tokens[k]);
                    if(wordsMap.containsKey(term)){

                    }

                }
            }




        }
    }

    public SparseInstances genSparseInstancesReview(JSONArray array) {




    }



}
