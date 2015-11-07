package textlib;

/**
 * Created by howard on 11/6/15.
 */
public class EnglishFilter {
    WordNetStem stemmer = null;
    float threshold = 0.5f;

    public EnglishFilter() throws Exception {
        stemmer = new WordNetStem();
    }

    public EnglishFilter(float threshold) throws Exception {
        this.threshold = threshold;
        stemmer = new WordNetStem();
    }

    public EnglishFilter(WordNetStem wordNetStem) throws Exception {
        stemmer = wordNetStem;
    }

    public EnglishFilter(WordNetStem wordNetStem, float threshold) throws Exception {
        this.threshold = threshold;
        stemmer = wordNetStem;
    }

    /*
    @return: true      is English or can not judge
             false     is not English
    * */
    public boolean filter(String text) throws Exception {
        if (text == null) {
            return false;
        }
        TextUtil textUtil = new TextUtil();
        String[] tokens = textUtil.tokenize(text.toLowerCase().replaceAll("_|-", " "));
        if (tokens == null) {
            return false;
        }
        String term = null;
        String word = null;
        int sumCount = 0;
        int missCount = 0;
        for (int k = 0; k < tokens.length; k++) {
            if (tokens[k] == null || tokens[k].isEmpty()) {
                continue;
            }
            word = tokens[k].replaceAll("[^A-Za-z0-9]", "");
            if(word.isEmpty()){
                continue;
            }
            term = stemmer.stemWord(word);
            sumCount++;
            if (term == null || term.isEmpty()) {
                missCount++;
            }
        }
        if (missCount > sumCount * threshold) {
            return false;
        }
        return true;
    }
}
