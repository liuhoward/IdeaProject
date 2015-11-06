package textlib;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by howard on 11/6/15.
 */
public class WordNetStem {

    public String WordNetDictPath = "../lib/WordNet_Dict_3.1";
    Dictionary WordNetDict = null;
    WordnetStemmer wordnetStemmer = null;
    private TextUtil textUtil = null;

    public WordNetStem() throws Exception{
        this.textUtil = new TextUtil();
        this.textUtil.importDict();
        this.textUtil.importFilterWords();
    }

    public WordNetStem(TextUtil util) throws Exception{
        this.textUtil = util;
        initialWordNetDict();
    }


    public void setWordNetDictPath(String dictPath){
        WordNetDictPath = dictPath;
    }

    public boolean initialWordNetDict() throws Exception{
        if(WordNetDict != null){
            return true;
        }
        WordNetDict = new Dictionary(new File(WordNetDictPath));
        if(WordNetDict == null){
            return false;
        }

        openWordNetDict();

        wordnetStemmer = new WordnetStemmer(WordNetDict);

        return true;
    }

    private boolean openWordNetDict() throws Exception{
        if(WordNetDict.isOpen()){
            return true;
        }

        WordNetDict.open();

        return true;
    }

    public boolean closeWordNetDict() throws Exception{

        WordNetDict.close();

        return true;
    }


    private String stemWordwithWordNet (String word) throws Exception{

        if(!openWordNetDict()){
            System.out.println("error: can not open WordNet dictionary\n");
            return null;
        }

        WordnetStemmer stemmer = wordnetStemmer;

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

        String term = textUtil.searchDictWord(word);
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

    public String stemfilter(String word) throws Exception{

        if(word == null || word.isEmpty()){
            return null;
        }

        if(textUtil.searchFilterWord(word)) {
            return null;
        }

        String term = textUtil.searchDictWord(word);
        if(term != null){
            if(textUtil.searchFilterWord(term)){
                return null;
            }
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

        if(textUtil.searchFilterWord(term)){
            return null;
        }

        return term;

    }

}
