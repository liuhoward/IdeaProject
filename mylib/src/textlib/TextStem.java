package textlib;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by howard on 9/17/15.
 */
public class TextStem {

    public String WordNetDictPath = "../lib/WordNet_Dict_3.1";
    Dictionary WordNetDict = null;
    public String hashDictPath = "../lib/hashdict.json";
    public String filterWordsPath = "../lib/stopwords";
    private int hashMapCapacity = 32768;
    private int hashSetCapacity = 1024;
    private HashMap<String, String> dictMap = null;
    private HashSet<String> filterSet = null;

    public void setWordNetDictPath(String dictPath){
        WordNetDictPath = dictPath;
    }


    public boolean importDict(String dictPath) throws Exception{

        if(dictPath == null){
            System.out.println("error: null " + dictPath + "\n");
        }

        JSONLib jsonLib = new JSONLib();
        JSONObject dictObj = jsonLib.parseObjectFile(dictPath);
        if(dictObj == null){
            System.out.println("error: fail to parse " + dictPath + "\n");
            dictMap = new HashMap<>(hashMapCapacity);
        }else{
            dictMap = new HashMap<>(dictObj);
        }

        int size = dictMap.size();

        System.out.println("imported dictionary size: " + size + "\n");

        return true;

    }

    public boolean exportDict(String dictPath) throws Exception{
        if(dictPath == null){
            System.out.println("error: null " + dictPath + "\n");
        }

        long size = dictMap.size();
        JSONObject object = new JSONObject(dictMap);
        JSONLib jsonLib = new JSONLib();
        boolean ret = jsonLib.formatObject(object, dictPath);
        if(!ret){
            System.out.println("export dictionary fail\n");
            return ret;
        }
        System.out.println("exported dictionary size: " + size + "\n");
        return true;

    }

    public boolean importFilterWords(String filterWordsPath) throws Exception{

        if(filterWordsPath == null){
            System.out.println("error: null " + filterWordsPath + "\n");
        }
        JSONLib jsonLib = new JSONLib();
        JSONArray filterArray = jsonLib.parseArrayFile(filterWordsPath);
        if(filterArray == null){
            System.out.println("warning: parse " + filterWordsPath + " fail\n");
            filterSet = new HashSet<>(hashSetCapacity);
        }else {
            filterSet = new HashSet<>(filterArray);
        }
        int size = filterSet.size();
        System.out.println("imported filter words size: " + size + "\n");

        return true;

    }

    public boolean initialWordNetDict() throws Exception{
        if(WordNetDict != null){
            return true;
        }
        WordNetDict = new Dictionary(new File(WordNetDictPath));
        if(WordNetDict == null){
            return false;
        }

        return true;
    }

    public boolean openWordNetDict() throws Exception{
        if(WordNetDict.isOpen()){
            return true;
        }

        WordNetDict.open();

        return true;
    }


    public String stemWordwithWordNet (String word) throws Exception{

        if(!openWordNetDict()){
            System.out.println("error: can not open WordNet dictionary\n");
            return null;
        }

        WordnetStemmer stemmer = new WordnetStemmer(WordNetDict);

        List<String> term = null;

        term = stemmer.findStems(word, POS.VERB);
        if(term != null && !term.isEmpty()){
            return term.get(0);
        }
        term = stemmer.findStems(word, POS.NOUN);
        if(term != null  && !term.isEmpty()){
            return term.get(0);
        }
        term = stemmer.findStems(word, POS.ADJECTIVE);
        if(term != null  && !term.isEmpty()){
            return term.get(0);
        }
        term = stemmer.findStems(word, POS.ADVERB);
        if(term != null  && !term.isEmpty()){
            return term.get(0);
        }

        return null;
    }

    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[0-9]+(\\.[0-9]+)?[A-Za-z]*");
        return pattern.matcher(str.replaceAll(",","")).matches();
    }

    public String stemWord(String word) throws Exception{

        if(word == null || word.isEmpty()){
            return null;
        }

        String term = dictMap.get(word);
        if(term != null){
            return term;
        }

        if(isDouble(word)){
            return null;
        }

        term = stemWordwithWordNet(word);

        /*
        if(term == null){
            Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
            term = (String)stemmer.stem(word);
        }*/

        if(term != null){
            //dictMap.put(word, term);
        }

        return term;

    }

    public void processDocs(JSONArray reviewArray, String destFile) throws Exception{

        float threshold = 0.5f;

        TextUtil util = new TextUtil();

        System.out.println(new Date() + "\n");
        System.out.println("start....................\n");

        Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
        FileOutputStream outFile = new FileOutputStream(destFile);
        BufferedWriter output = new BufferedWriter((new OutputStreamWriter(outFile)));
        output.write("[\n");

        String idkey = "id";
        String reviewKey = "review";
        int size = 0;
        for (JSONObject jsonObj : (List<JSONObject>) reviewArray) {
            long id = (long)jsonObj.get(idkey);
            String review = (String)jsonObj.get(reviewKey);
            if(review == null){
                continue;
            }
            int missCount = 0;
            int sumCount = 0;
            String[] sentences =
                    util.sentenceDetect(review.toLowerCase().replaceAll("[^\\x20-\\x7E]", " "));
            if(sentences == null){
                continue;
            }
            StringBuffer sb = new StringBuffer(256);
            String term = null;
            for(int j = 0; j < sentences.length; j++){
                String[] tokens = util.tokenize(sentences[j].replaceAll("_|-", " "));
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

            if(missCount > sumCount * threshold){
                sb = null;
                continue;
            }
            JSONObject newObj = new JSONObject();
            newObj.put(idkey, id);
            newObj.put(reviewKey, sb.toString());
            if(size > 0){
                output.write(",\n");
            }
            TreeMap<String, Object> treeMap = new TreeMap<>(newObj);
            output.write(JSONValue.toJSONString(treeMap));
            size++;
            sb = null;
            newObj = null;
        }
        output.write("\n]");

        output.flush();
        output.close();

        //exportDict(hashDictPath.replace(".json", "Out.json"));

        System.out.println(new Date() + "\n");
        System.out.println("finish....................\n");

    }


    public void processDocs(String srcFile, String destFile) throws Exception{

        initialWordNetDict();
        importDict(hashDictPath);

        JSONLib lib = new JSONLib();

        JSONArray reviewArray = lib.parseArrayFile(srcFile);
        if(reviewArray == null){
            System.out.println("error: parse " + srcFile + " fail\n");
        }

        processDocs(reviewArray, destFile);

    }

    public static void main(String[] args) throws Exception{

        /*
        String dictpath = "../lib/hashdict.json";
        String dictpathout = dictpath.replace(".json", "Out.json");

        JSONLib lib = new JSONLib();
        lib.formatObject(dictpath, dictpathout);
        */

        String dataPath = "../data/review/";
        String srcFile = dataPath + "tripadvisor.json";
        String destFile = srcFile.replace(".json", "Stem.json");

        TextStem textStem = new TextStem();
        textStem.initialWordNetDict();
        textStem.importDict(textStem.hashDictPath);

        textStem.processDocs(srcFile, destFile);

        srcFile = dataPath + "yelp.json";
        destFile = srcFile.replace(".json", "Stem.json");

        textStem.processDocs(srcFile, destFile);


    }



}
