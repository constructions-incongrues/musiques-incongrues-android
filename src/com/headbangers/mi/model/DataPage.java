package com.headbangers.mi.model;

import java.util.ArrayList;
import java.util.List;

public class DataPage {

    private List<MILinkData> data = new ArrayList<MILinkData>();
    private int nbTotal = 0;
    
    public DataPage(List<MILinkData> data, int nbTotal) {
        this.data = data;
        this.nbTotal = nbTotal;
    }
    
    public DataPage() {
    }

    public void add (MILinkData data){
        this.data.add(data);
    }
    
    public void setTotal (int total){
        this.nbTotal = total;
    }
    
    public List<MILinkData> getData() {
        return data;
    }
     
    public int getNbTotal() {
        return nbTotal;
    }
}
