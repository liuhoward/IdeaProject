package textlib;

import org.json.simple.JSONArray;

import java.util.concurrent.CountDownLatch;

/**
 * Created by howard on 10/5/15.
 */
public class MultiThread extends Thread{

    public TextStem textStem = null;
    public JSONArray jsonArray = null;
    public String destFile = null;
    public CountDownLatch countDownLatch = null;

    public MultiThread(String name, TextStem textStem, JSONArray array, String destFile, CountDownLatch countDownLatch){
        this.textStem = textStem;
        this.setName(name);
        this.jsonArray = array;
        this.destFile = destFile;
        this.countDownLatch = countDownLatch;
    }

    public void setTextStem(TextStem textStem){
        this.textStem = textStem;
    }

    public void run() {
        if(this.jsonArray == null){
            System.out.println("error: array is null\n");
            return;
        }
        System.out.println("start thread " + getName() + "\n");

        try {
            textStem.processDocs(this.jsonArray, destFile);
        } catch (Exception e){
            System.out.println("fail\n");
        }
        countDownLatch.countDown();

    }

    public static void main(String[] args) throws Exception {

        /*
        String dictpath = "../lib/hashdict.json";
        String dictpathout = dictpath.replace(".json", "Out.json");

        JSONLib lib = new JSONLib();
        lib.formatObject(dictpath, dictpathout);
        */

        int num_threads = 6;

        String dataPath = "../data/review/";
        String srcFile = dataPath + "tripadvisor.json";
        String destFile = srcFile.replace(".json", "Stem.json");

        TextStem textStem = new TextStem();
        textStem.initialWordNetDict();
        textStem.importDict(textStem.hashDictPath);

        JSONLib lib = new JSONLib();

        JSONArray reviewArray = lib.parseArrayFile(srcFile);
        if (reviewArray == null) {
            System.out.println("error: parse " + srcFile + " fail\n");
        }

        CountDownLatch countDownLatch = new CountDownLatch(num_threads);

        int size = reviewArray.size();
        int groupSize = size / num_threads + 1;
        for (int i = 0; i < num_threads; i++) {
            int from = i * groupSize;
            int end = from + groupSize;
            if (end > size) {
                end = size;
            }
            JSONArray tmpArray = new JSONArray();
            tmpArray.addAll(reviewArray.subList(from, end));
            String tmp = String.valueOf(i) + ".json";
            MultiThread tmpThread = new MultiThread(String.valueOf(i), textStem, tmpArray, destFile.replace(".json", tmp), countDownLatch);
            tmpThread.start();
        }

        try
        {
            countDownLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        JSONArray newArray = new JSONArray();
        for(int i = 0; i < num_threads; i++){
            String tmp = String.valueOf(i) + ".json";
            JSONArray tmpArray = lib.parseArrayFile(destFile.replace(".json", tmp));
            newArray.addAll(tmpArray);
        }

        lib.formatArray(newArray, destFile);

    }

}
