package datalib;

import weka.core.SparseInstance;

/**
 * Created by howard on 10/12/15.
 */
public class ESparseInstance extends SparseInstance {

    private String instanceID = "defaultID";

    public ESparseInstance(String id, SparseInstance instance){
        super(instance);
        setInstanceID(id);
    }

    public ESparseInstance(ESparseInstance instance){
        super(instance);
        instanceID = instance.instanceID;
    }

    public ESparseInstance(String id, double weight, double[] attValues){
        super(weight, attValues);
        instanceID = id;
    }

    public ESparseInstance(String id, double weight, double[] attValues,
                           int[] indices, int numAttribute){
        super(weight, attValues, indices, numAttribute);
        instanceID = id;
    }


    public String getInstanceID(){
        return instanceID;
    }
    public void setInstanceID(String id){
        this.instanceID = id;
    }


}
