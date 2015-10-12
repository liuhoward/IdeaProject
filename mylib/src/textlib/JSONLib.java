package textlib;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.*;

/**
 * Created by howard on 10/2/15.
 */
public class JSONLib {

    /**
     * Parse a Json file.
     */
    public JSONArray parseJsonFile(String jsonFilePath) throws Exception{

        FileInputStream jsonFile = new FileInputStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        Object fileObj = JSONValue.parse(readerJson);
        JSONArray arrayObj = (JSONArray) fileObj;
        return arrayObj;

    }

    public JSONArray parseArrayFile(String filePath) throws Exception{

        if(filePath == null){
            System.out.println("error: null " + filePath + "\n");
        }

        FileInputStream jsonFile = new FileInputStream(filePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        Object fileObj = JSONValue.parse(readerJson);
        JSONArray array = (JSONArray) fileObj;

        return array;

    }


    public JSONObject parseObjectFile(String filePath) throws Exception{

        if(filePath == null){
            System.out.println("error: null " + filePath + "\n");
        }

        FileInputStream jsonFile = new FileInputStream(filePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        Object fileObj = JSONValue.parse(readerJson);
        JSONObject object = (JSONObject) fileObj;

        return object;

    }

    public boolean formatArray(JSONArray array, String destFile) throws Exception{

        System.out.println(new Date() + "\tformat JSON array start\n");
        if(array == null){
            System.out.println("error: array is null\n");
            return false;
        }

        int size = array.size();

        FileOutputStream outFile = new FileOutputStream(destFile);
        BufferedWriter output = new BufferedWriter((new OutputStreamWriter(outFile)));
        output.write("[\n");

        JSONObject object = (JSONObject)array.get(0);
        TreeMap<String, Object> treeMap = new TreeMap<>(object);
        output.write(JSONValue.toJSONString(treeMap));
        for(int i = 1; i < size; i++){
            output.write(",\n");
            object = (JSONObject)array.get(i);
            treeMap = new TreeMap<>(object);
            output.write(JSONValue.toJSONString(treeMap));

        }
        output.write("\n]");

        output.flush();
        output.close();
        array = null;

        System.out.println(new Date() + "\tformat JSON array OK size: " + size + "\n");
        return true;
    }

    public boolean formatArray(String srcFile, String destFile) throws Exception{

        JSONArray array = parseArrayFile(srcFile);
        if(array == null){
            System.out.println("error: parseArrayFile fail\n");
            return false;
        }

        int size = array.size();

        if(!formatArray(array, destFile)){
            System.out.println(new Date() + "\tformat JSON array " + srcFile + " fail\n");
        }

        System.out.println(new Date() + "\tformat JSON array " + srcFile + " OK size: " + size + "\n");
        return true;
    }

    public boolean formatObject(JSONObject object, String destFile) throws Exception{

        System.out.println(new Date() + "\tformat JSON array start\n");

        if(object == null){
            System.out.println("error: parseArrayFile fail\n");
            return false;
        }

        FileOutputStream outFile = new FileOutputStream(destFile);
        BufferedWriter output = new BufferedWriter((new OutputStreamWriter(outFile)));
        output.write("{\n");

        TreeMap<String, Object> treeSet = new TreeMap<>(object);
        Iterator iter = treeSet.entrySet().iterator();
        Map.Entry entry = null;
        int size = 0;
        while(iter.hasNext()){
            entry = (Map.Entry)iter.next();
            if(size > 0){
                output.write(",\n");
            }
            output.write(JSONObject.toString(String.valueOf(entry.getKey()), entry.getValue()));
            size++;
        }
        output.write("\n}");

        output.flush();
        output.close();

        System.out.println(new Date() + "\tformat JSON object OK size: " + size + "\n");
        return true;
    }

    public boolean formatObject(String srcFile, String destFile) throws Exception{

        JSONObject object = parseObjectFile(srcFile);
        if(object == null){
            System.out.println("error: parseArrayFile fail\n");
            return false;
        }
        int size = object.size();

        if(!formatObject(object, destFile)){
            System.out.println(new Date() + "\tformat JSON array " + srcFile + " fail\n");
        }

        System.out.println(new Date() + "\tformat JSON object " + srcFile + " OK size: " + size + "\n");
        return true;
    }

}
