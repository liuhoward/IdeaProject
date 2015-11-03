package textlib;

import datalib.ESparseInstance;
import datalib.SparseInstances;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.*;

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
        HashMap<String, Integer> wordsMap = new HashMap<>(mapCap);
        HashMap<Integer, String> wordsReverseMap = new HashMap<>(mapCap);

        TextUtil textUtil = new TextUtil();
        SimpleStem simpleStem = new SimpleStem(textUtil);
        int size = array.size();
        int attDefault = 32768;
        SparseInstances instData = new SparseInstances(attDefault, size * 5);

        int attIndex = 0;
        int attCurIndex = 0;
        int senIndex = 0;
        int senCurIndex = 0;
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
            String term = null;
            for(int j = 0; j < sentences.length; j++){
                String[] tokens = textUtil.tokenize(sentences[j].replaceAll("[.,!?:;_-]", " "));
                if(tokens == null){
                    continue;
                }
                HashMap<Integer, Integer> senMap = new HashMap<>(30);
                double[] tokenWeghts = new double[tokens.length];
                for(int k = 0; k < tokens.length; k++){
                    senIndex = 0;
                    if(tokens[k].isEmpty()){
                        continue;
                    }
                    if(textUtil.searchFilterWord(tokens[k])){
                        continue;
                    }
                    term = simpleStem.stem(tokens[k]);
                    if(term == null){
                        continue;
                    }
                    if(textUtil.searchFilterWord(term)){
                        continue;
                    }

                    if(!wordsMap.containsKey(term)){
                        wordsMap.put(term, attIndex);
                        wordsReverseMap.put(attIndex, term);
                        attIndex++;
                    }
                    attCurIndex = wordsMap.get(term);
                    if(senMap.containsKey(attCurIndex)){
                        senCurIndex = senMap.get(attCurIndex);
                        tokenWeghts[senCurIndex]++;
                    }else {
                        senMap.put(attCurIndex, senIndex);
                        tokenWeghts[senIndex] = 1;
                        senIndex++;
                    }
                }
                int[] indices = new int[senMap.size()];
                double[] attValues = new double[senMap.size()];
                TreeMap<Integer, Integer> treeMap = new TreeMap<>(senMap);
                Iterator iter = treeMap.keySet().iterator();
                int index = 0;
                while(iter.hasNext()){
                    int key = (Integer)iter.next();
                    int value = (Integer)treeMap.get(key);
                    indices[index] = key;
                    attValues[index] = tokenWeghts[value];
                    index++;
                }
                ESparseInstance instance = new ESparseInstance(String.valueOf(id), 1.0, attValues, indices, indices.length);
                instData.addInstance(instance);

            }
        }
        TreeMap<Integer, String> termMap = new TreeMap<>(wordsReverseMap);
        Iterator iterator = termMap.keySet().iterator();
        while(iterator.hasNext()){
            int key = (Integer)iterator.next();
            instData.addAttribute(termMap.get(key));
        }

        return instData;
    }

    public SparseInstances genSparseInstancesReview(JSONArray array) throws Exception{

        if(array == null){
            System.out.println("error: array is null!\n");
            return null;
        }

        int mapCap = 65536;
        HashMap<String, Integer> wordsMap = new HashMap<>(mapCap);
        HashMap<Integer, String> wordsReverseMap = new HashMap<>(mapCap);

        TextUtil textUtil = new TextUtil();
        SimpleStem simpleStem = new SimpleStem(textUtil);
        int size = array.size();
        int attDefault = 32768;
        SparseInstances instData = new SparseInstances(attDefault, size * 5);

        int attIndex = 0;
        int attCurIndex = 0;
        int senIndex = 0;
        int senCurIndex = 0;
        String idkey = "id";
        String reviewKey = "review";
        for (JSONObject jsonObj : (List<JSONObject>) array) {
            long id = (long) jsonObj.get(idkey);
            String review = (String) jsonObj.get(reviewKey);
            if (review == null) {
                continue;
            }

            String term = null;

            String[] tokens = textUtil.tokenize(review.toLowerCase().replaceAll("[.,!?:;_-]", " "));
            if (tokens == null) {
                continue;
            }
            HashMap<Integer, Integer> senMap = new HashMap<>(30);
            double[] tokenWeghts = new double[tokens.length];
            for (int k = 0; k < tokens.length; k++) {
                senIndex = 0;
                if (tokens[k].isEmpty()) {
                    continue;
                }
                if (textUtil.searchFilterWord(tokens[k])) {
                    continue;
                }
                term = simpleStem.stem(tokens[k]);
                if (term == null) {
                    continue;
                }
                if (textUtil.searchFilterWord(term)) {
                    continue;
                }

                if (!wordsMap.containsKey(term)) {
                    wordsMap.put(term, attIndex);
                    wordsReverseMap.put(attIndex, term);
                    attIndex++;
                }
                attCurIndex = wordsMap.get(term);
                if (senMap.containsKey(attCurIndex)) {
                    senCurIndex = senMap.get(attCurIndex);
                    tokenWeghts[senCurIndex]++;
                } else {
                    senMap.put(attCurIndex, senIndex);
                    tokenWeghts[senIndex] = 1;
                    senIndex++;
                }
            }
            int[] indices = new int[senMap.size()];
            double[] attValues = new double[senMap.size()];
            TreeMap<Integer, Integer> treeMap = new TreeMap<>(senMap);
            Iterator iter = treeMap.keySet().iterator();
            int index = 0;
            while (iter.hasNext()) {
                int key = (Integer) iter.next();
                int value = (Integer) treeMap.get(key);
                indices[index] = key;
                attValues[index] = tokenWeghts[value];
                index++;
            }
            ESparseInstance instance = new ESparseInstance(String.valueOf(id), 1.0, attValues, indices, indices.length);
            instData.addInstance(instance);


        }
        TreeMap<Integer, String> termMap = new TreeMap<>(wordsReverseMap);
        Iterator iterator = termMap.keySet().iterator();
        while(iterator.hasNext()){
            int key = (Integer)iterator.next();
            instData.addAttribute(termMap.get(key));
        }

        return instData;
    }



}
