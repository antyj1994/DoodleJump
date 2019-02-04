package com.ilmale.doodlejump.domain;

import com.ilmale.doodlejump.Constants;

import java.util.StringTokenizer;

public class RemotePlayer {

    public float pX;
    public float pY;
    public int x; //VARIABILE DI CONTRLLO PER IL BITMAP

    private Constants constants = Constants.getInstance();

    public void update(){



    }

    public void setpX(float pX) {
        this.pX = pX;
    }

    public void setpY(float pY) {
        this.pY = pY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void receiveMessage(String s){

        StringTokenizer st = new StringTokenizer(s, " ", false);
        setpX(Float.parseFloat(st.nextToken()));
        setpY(Float.parseFloat(st.nextToken()) + constants.getPoints());
        setX(Integer.parseInt(st.nextToken()));

    }

}