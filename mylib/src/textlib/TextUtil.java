package textlib;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by howard on 10/24/15.
 */
public class TextUtil {

    public String hashDictPath = "../lib/hashdict.json";
    public String filterWordsPath = "../lib/stopwords";
    private int hashMapCapacity = 32768;
    private int hashSetCapacity = 1024;
    private HashMap<String, String> dictMap = null;
    private HashSet<String> filterSet = null;


    public boolean importDict(String dictPath) throws Exception{

        if(dictPath == null){
            System.out.println("warning: null " + dictPath + "\n");
            dictPath = hashDictPath;
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

    public boolean importDict() throws Exception{

        String dictPath = hashDictPath;

        return importDict(dictPath);

    }


    public boolean exportDict(String dictPath) throws Exception{
        if(dictPath == null){
            System.out.println("warning: null " + dictPath + "\n");
            dictPath = hashDictPath.replace(".json", "Out.json");
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

    public boolean exportDict() throws Exception{

        String dictPath = hashDictPath.replace(".json", "Out.json");

        return exportDict(dictPath);

    }

    public boolean importFilterWords(String filterWordsPath) throws Exception{

        if(filterWordsPath == null){
            System.out.println("warning: null " + filterWordsPath + "\n");
            filterWordsPath = this.filterWordsPath;
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

    public boolean importFilterWords() throws Exception{

        String filterWordsPath = this.filterWordsPath;

        return importFilterWords(filterWordsPath);
    }


    public String[] tokenize(String sentence) throws Exception {
        InputStream is = new FileInputStream("../lib/opennlp/apache-opennlp-1.6.0/en-token.bin");

        TokenizerModel model = new TokenizerModel(is);

        Tokenizer tokenizer = new TokenizerME(model);

        String tokens[] = tokenizer.tokenize(sentence);

        is.close();

        return tokens;
    }

    public String[] sentenceDetect(String s) throws Exception {


        InputStream is = new FileInputStream("../lib/opennlp/apache-opennlp-1.6.0/en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(s);


        is.close();

        return sentences;
    }
}
