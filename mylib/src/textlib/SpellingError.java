package textlib;

import com.atlascopco.hunspell.Hunspell;

import java.util.List;

/**
 * Created by howard on 11/7/15.
 */
public class SpellingError {

    private String dictionPath = "../lib/HunspellBridJ/en_US/";
    private Hunspell speller = null;

    public SpellingError(){
        speller = new Hunspell(dictionPath + "en_US.dic", dictionPath + "en_US.aff");
    }

    public SpellingError(String dir) {
        this.dictionPath = dir;
        speller = new Hunspell(dir + "en_US.dic", dir + "en_US.aff");
    }

    public boolean checkSpell(String word) {
        return speller.spell(word);
    }

    public List<String> suggest(String word) {
        return speller.suggest(word);
    }

    public int checkText(String text) throws Exception{
        int count = 0;
        TextUtil textUtil = new TextUtil();
        String[] tokens = textUtil.tokenize(text.toLowerCase().replaceAll("_|-", " "));
        String term = null;
        for(int index = 0; index < tokens.length; index++){
            term = tokens[index].replaceAll("[^A-Za-z0-9]", "");
            if(term == null || term.isEmpty()){
                continue;
            }
            if(!speller.spell(term)) {
                count++;
            }
        }

        return count;
    }

    public void close() {
        speller.close();
    }

}
