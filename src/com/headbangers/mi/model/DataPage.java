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
    
    public MILinkData findInList (int position){
        if (data!=null){
            return data.get(position);
        }
        return null;
    }
    
    public int size(){
        if (data!=null){
            return data.size();
        }
        return 0;
    }
     
    public int getNbTotal() {
        return nbTotal;
    }

    public void filter(int offset, int max) {
        this.data = this.data.subList(offset, offset+max);        
    }
}
