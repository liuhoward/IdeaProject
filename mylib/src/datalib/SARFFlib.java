package datalib;

import java.io.*;

/**
 * Created by howard on 10/12/15.
 */
public class SARFFlib {

    private SparseInstances m_Data;

    public SARFFlib(){
        m_Data = new SparseInstances();
    }

    public boolean loadSARFF(String srcFile) throws Exception{

        BufferedReader reader = new BufferedReader(new FileReader(srcFile));

        String line = null;

        while((line = reader.readLine()) != null){

            if(line.startsWith(m_Data.SARFF_Attribute)){
                while((line = reader.readLine()) != null){
                    if(line.startsWith(m_Data.SARFF_DATA)){
                        break;
                    }
                    String attribute = line.trim();
                    m_Data.addAttribute(attribute);
                }
            }

            int maxnumAtt = m_Data.numAttributes();

            if(line.startsWith(m_Data.SARFF_DATA)){
                while((line = reader.readLine()) != null){
                    if(line.startsWith(m_Data.SARFF_Attribute)){
                        break;
                    }
                    String[] fields = line.trim().split(",");
                    int size = fields.length - 1;
                    if(size == 0){
                        continue;
                    }
                    int[] indices = new int[size];
                    double[] attValues = new double[size];
                    for( int i = 2; i < size; i++){
                        String[] parts = fields[i].split(" ");
                        indices[i] = Integer.valueOf(parts[0]);
                        if(indices[i] >= maxnumAtt){
                            System.out.println("error: index is out of range\n");
                            return false;
                        }
                        attValues[i] = Double.valueOf(parts[1]);
                    }
                    double weight = Double.valueOf(fields[1]);
                    ESparseInstance instance = new ESparseInstance(fields[0].trim(), weight, attValues, indices, size);

                }
            }

        }

        reader.close();

        return true;

    }

    public boolean saveSARFF(String destFile) throws Exception{

        return saveSARFF(m_Data, destFile);
    }

    public static boolean saveSARFF(SparseInstances data, String destFile) throws Exception{

        BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
        StringBuffer sb = new StringBuffer(1024);
        int numAtt = data.numAttributes();
        int numInst = data.numInstances();
        sb.append(data.SARFF_Attribute + "\n");
        for(int attIndex = 0; attIndex < numAtt; attIndex++){
            sb.append(data.attribute(attIndex).name() + "\n");
        }
        sb.append("\n");
        sb.append(data.SARFF_DATA + "\n");
        writer.write(sb.toString());
        for(int instIndex = 0; instIndex < numInst; instIndex++){
            sb = new StringBuffer(1024);
            ESparseInstance instance = data.getESparseInstance(instIndex);
            sb.append(instance.getInstanceID() + ",");
            sb.append(instance.weight() + ",");
            int numValues = instance.numValues();
            for(int sparseIndex = 0; sparseIndex < numValues; sparseIndex++){
                int attIndex = instance.index(sparseIndex);
                double value = instance.value(attIndex);
                sb.append(sparseIndex + " " + value + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
            writer.write(sb.toString());
        }

        writer.flush();
        writer.close();

        return true;
    }

    public SparseInstances getSparseInstances(){
        return m_Data;
    }

    public boolean setSparseInstaces(SparseInstances data){
        if(data == null){
            return false;
        }

        m_Data = data;
        return true;
    }

}
