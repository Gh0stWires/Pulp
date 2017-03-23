package tk.samgrogan.pulp.Models;

import java.util.List;

/**
 * Created by ghost on 3/22/2017.
 */

public class ComicDataObject {
    public String collectionTitle;
    public List<String> collectionList;

    public ComicDataObject(){

    }

    public ComicDataObject(String collectionTitle, List<String> collectionList){
        this.collectionTitle = collectionTitle;
        this.collectionList = collectionList;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public List<String> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(List<String> collectionList) {
        this.collectionList = collectionList;
    }
}
