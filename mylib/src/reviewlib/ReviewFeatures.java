package reviewlib;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import textlib.JSONLib;
import textlib.SpellingError;
import textlib.TextUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by howard on 11/7/15.
 */
public class ReviewFeatures {

    private ConcurrentHashMap<String, Boolean> spellMap = null;
    private int capacity = 262144;
    public ReviewFeatures() {
        this.spellMap = new ConcurrentHashMap<>(capacity);
    }

    public ReviewFeatures(ConcurrentHashMap<String, Boolean> map) {
        this.spellMap = map;
    }

    public void exportSpellDict(String destFile) throws Exception{
        JSONObject obj = new JSONObject(spellMap);
        JSONLib.formatObject(obj, destFile);
    }

    public void calFeatures(JSONArray reviewArray, String destFile) throws Exception{
        BufferedWriter reviewOut = new BufferedWriter(new FileWriter(destFile));
        //reviewOut.write("id,numWords,numSentences,avgWordLen,avgSentenceLen,spellingErrors,AutomatedReadabilityIndex\n");
        reviewOut.flush();
        TextUtil textUtil = new TextUtil();
        SpellingError spellChecker = new SpellingError();
        StringBuffer sb = null;
        boolean ret = true;
        String idKey = "id";
        String reviewKey = "review";
        int size = 0;
        for (JSONObject jsonObj : (List<JSONObject>) reviewArray) {
            int numWords = 0;
            int numSen = 0;
            float avgWordLen = 0;
            float avgSenLen = 0;
            int spellingError = 0;
            float arIndex = 0;
            long id = (long)jsonObj.get(idKey);
            String review = (String)jsonObj.get(reviewKey);
            String[] sentences = textUtil.sentenceDetect(review.toLowerCase().replaceAll("[^\\x20-\\x7E]", " "));
            for(int senIndex = 0; senIndex < sentences.length; senIndex++) {
                String[] tokens = textUtil.tokenize(sentences[senIndex].replaceAll("_|-", " "));
                if(tokens == null){
                    continue;
                }
                int tmpTokenNum = 0;
                for(int k = 0; k < tokens.length; k++){
                    if(tokens[k] == null){
                        continue;
                    }
                    String term = tokens[k].replaceAll("[^A-Za-z0-9']", "");
                    if(term.isEmpty()){
                        continue;
                    }
                    if(spellMap.containsKey(term)){
                        ret = spellMap.get(term);
                    }else {
                        ret = spellChecker.checkSpell(term);
                        spellMap.put(term, ret);
                    }
                    if(!ret){
                        spellingError++;
                    }
                    tmpTokenNum++;
                    avgWordLen += term.length();
                }
                if(tmpTokenNum > 0){
                    numWords += tmpTokenNum;
                    numSen++;
                }
            }
            avgWordLen = avgWordLen * 1.0f / numWords;
            avgSenLen = numWords * 1.0f / numSen;
            arIndex = 4.71f * avgWordLen + 0.5f * avgSenLen - 21.43f;

            sb = new StringBuffer(128);
            sb.append(String.valueOf(id) + ",");
            sb.append(String.valueOf(numWords) + ",");
            sb.append(String.valueOf(numSen) + ",");
            sb.append(String.valueOf(avgWordLen) + ",");
            sb.append(String.valueOf(avgSenLen) + ",");
            sb.append(String.valueOf(spellingError) + ",");
            sb.append(String.valueOf(arIndex) + "\n");

            reviewOut.write(sb.toString());
            size++;
        }
        reviewOut.flush();
        reviewOut.close();

        spellChecker.close();

        System.out.println(new Date() + "  finished, size: " + size + "\n");
    }

    public static void main(String[] args) throws Exception{
        String dataPath = "../data/review/";
        String srcFile1 = dataPath + "tripadvisorEn.json";
        String srcFile2 = dataPath + "yelpEn.json";
        String destFile = dataPath + "reviewfeatures.txt";

        JSONArray reviewArray = JSONLib.parseArrayFile(srcFile1);
        JSONArray jsonArray = JSONLib.parseArrayFile(srcFile2);
        if(reviewArray == null || jsonArray == null){
            System.out.println("error: parse json files fail\n");
        }
        reviewArray.addAll(jsonArray);
        System.out.println(new Date() + " review array size: " + reviewArray.size() + "\n");

        ReviewFeatures reviewFeatures = new ReviewFeatures();
        reviewFeatures.calFeatures(reviewArray, destFile);

        reviewFeatures.exportSpellDict("../lib/spellDict.json");

        System.out.println(new Date() + "  finished\n");
    }

}
