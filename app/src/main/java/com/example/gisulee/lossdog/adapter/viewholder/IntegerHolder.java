package com.example.gisulee.lossdog.adapter.viewholder;

public class IntegerHolder {

    public Integer value;

    public IntegerHolder(Integer value){
        this.value = value;
    }

    @Override
    public String toString(){
        return String.valueOf(value);
    }
}
