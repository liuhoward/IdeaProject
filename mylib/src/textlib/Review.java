package textlib;

/**
 * Created by howard on 9/22/15.
 */

import java.io.Serializable;

public class Review implements Serializable {
    private long id;
    private String review;

    public Review(){

    }

    public Review(long id, String review){
        this.id = id;
        this.review = review;
    }

    public void setID(long id){
        this.id=id;
    }
    public long getID(){
        return id;
    }

    public void setReview(String review){
        this.review = review;
    }
    public String getReview(){
        return review;
    }
}
