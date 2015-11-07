package reviewlib;

import org.json.simple.JSONArray;
import textlib.JSONLib;

import java.io.*;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by howard on 11/7/15.
 */
public class FeaturesThread extends Thread {
    public JSONArray reviewArray = null;
    public String destFile = null;
    public ReviewFeatures reviewFeatures = null;
    public CountDownLatch countDownLatch = null;

    public FeaturesThread(String name, JSONArray reviewArray, String destFile,
                          ReviewFeatures reviewFeatures, CountDownLatch countDownLatch) {
        this.setName(name);
        this.reviewArray = reviewArray;
        this.destFile = destFile;
        this.reviewFeatures = reviewFeatures;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        if(this.reviewArray == null || this.reviewFeatures == null || destFile == null){
            System.out.println("error: \n");
            return;
        }
        System.out.println("start thread " + this.getName() + "\n");

        try {
            reviewFeatures.calFeatures(this.reviewArray, this.destFile);
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        countDownLatch.countDown();
    }

    public static void main(String[] args) throws Exception{
        String dataPath = "../data/review/";
        String srcFile1 = dataPath + "tripadvisorEn.json";
        String srcFile2 = dataPath + "yelpEn.json";
        String destFile = dataPath + "reviewfeatures.txt";

        int num_threads = 6;
        ReviewFeatures reviewFeatures = new ReviewFeatures();
        JSONArray reviewArray = JSONLib.parseArrayFile(srcFile1);
        JSONArray jsonArray = JSONLib.parseArrayFile(srcFile2);
        if(reviewArray == null || jsonArray == null){
            System.out.println("error: parse json files fail\n");
        }
        reviewArray.addAll(jsonArray);
        System.out.println(new Date() + " review array size: " + reviewArray.size() + "\n");
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
            String tmp = String.valueOf(i) + ".txt";
            FeaturesThread tmpThread = new FeaturesThread(String.valueOf(i), tmpArray, destFile.replace(".txt", tmp), reviewFeatures, countDownLatch);
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

        BufferedWriter output = new BufferedWriter(new FileWriter(destFile));
        output.write("id,numWords,numSentences,avgWordLen,avgSentenceLen,spellingErrors,AutomatedReadabilityIndex\n");
        BufferedReader input = null;
        for(int i = 0; i < num_threads; i++){
            String tmp = String.valueOf(i) + ".txt";
            input = new BufferedReader(new FileReader(destFile.replace(".txt", tmp)));
            String line = null;

            while ((line = input.readLine()) != null){
                output.write(line);
            }
            input = null;
        }
        output.flush();
        output.close();

    }

}
