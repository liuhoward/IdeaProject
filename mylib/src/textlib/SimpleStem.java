package textlib;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Created by howard on 11/2/15.
 */
public class SimpleStem {

    private TextUtil textUtil = null;
    private SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    public SimpleStem() throws Exception{
        this.textUtil = new TextUtil();
        textUtil.importDiffsStopwords();
    }

    public SimpleStem(TextUtil util) throws Exception{
        this.textUtil = util;
        textUtil.importDiffsStopwords();
    }

    public String stem(String word){
        if(word == null || word.isEmpty()){
            return null;
        }

        String term = textUtil.searchDictWord(word);
        if(term != null){
            return term;
        }

        term = (String)stemmer.stem(word);

        return term;

    }

}
