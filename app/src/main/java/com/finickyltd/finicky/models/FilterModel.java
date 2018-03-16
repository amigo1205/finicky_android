package com.finickyltd.finicky.models;

/**
 * Created by common on 9/5/2017.
 */

public class FilterModel {

    // filter options
    int celery;
    int crustacean;
    int dairy;
    int egg;
    int fish;
    int gluten;
    int lupin;
    int mollusc;
    int mustard;
    int peanut;
    int sesame;
    int soya;
    int sulphites;
    int treenut;
    int vegan;
    int honey;
    int palmOil;
    int sugar;

    String celeryIng;
    String crustaceanIng;
    String dairyIng;
    String eggIng;
    String fishIng;
    String glutenIng;
    String lupinIng;
    String molluscIng;
    String mustardIng;
    String peanutIng;
    String sesameIng;
    String soyaIng;
    String sulphitesIng;
    String treenutIng;
    String veganIng;

    // result
    int success;
    public int getSuccess() {
        return this.success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getVegan() {
        return this.vegan;
    }

    public String getVeganIng(){return  this.veganIng;}

    public void setVegan(int vegan) {
        this.vegan = vegan;
    }

    public int getFish() {
        return this.fish;
    }

    public String getFishIng(){return  this.fishIng;}

    public void setFish(int fish) {
        this.fish = fish;
    }

    public int getCrustacean() {
        return this.crustacean;
    }

    public String getCrustaceanIng(){return  this.crustaceanIng;}

    public void setCrustacean(int crustacean) {
        this.crustacean = crustacean;
    }

    public int getMollusc() {
        return this.mollusc;
    }

    public String getMolluscIng(){return  this.molluscIng;}

    public void setMollusc(int mollusc) {
        this.mollusc = mollusc;
    }

    public int getGluten() {
        return this.gluten;
    }

    public String getGlutenIng(){return  this.glutenIng;}

    public void setGluten(int gluten) {
        this.gluten = gluten;
    }

    public int getDairy() {
        return this.dairy;
    }

    public String getDairyIng(){return  this.dairyIng;}

    public void setDairy(int dairy) {
        this.dairy = dairy;
    }

    public int getCelery() {
        return this.celery;
    }

    public String getCeleryIng(){return  this.celeryIng;}

    public void setCelery(int celery) {
        this.celery = celery;
    }

    public int getLupin() {
        return this.lupin;
    }

    public String getLupinIng(){return  this.lupinIng;}

    public void setLupin(int lupin) {
        this.lupin = lupin;
    }

    public int getSesame() {
        return this.sesame;
    }

    public String getSesameIng(){return  this.sesameIng;}

    public void setSesame(int sesame) {
        this.sesame = sesame;
    }

    public int getSulphites() {
        return this.sulphites;
    }

    public String getSulphitesIng(){return  this.sulphitesIng;}

    public void setSulphites(int sulphites) {
        this.sulphites = sulphites;
    }

    public int getEgg() {
        return this.egg;
    }

    public String getEggIng(){return  this.eggIng;}

    public void setEgg(int egg) {
        this.egg = egg;
    }

    public int getSoya() {
        return this.soya;
    }

    public String getSoyaIng(){return  this.soyaIng;}

    public void setSoya(int soya) {
        this.soya = soya;
    }

    public int getTreenut() {
        return this.treenut;
    }

    public String getTreenutIng(){return  this.treenutIng;}

    public void setTreenut(int treenut) {
        this.treenut = treenut;
    }

    public int getPeanut() {
        return this.peanut;
    }

    public String getPeanutIng(){return  this.peanutIng;}

    public void setPeanut(int peanut) {
        this.peanut = peanut;
    }

    public int getMustard() {
        return this.mustard;
    }

    public String getMustardIng(){return  this.mustardIng;}

    public void setMustard(int mustard) {
        this.mustard = mustard;
    }

    public int getHoney() {
        return honey;
    }

    public int getPalmOil() {
        return palmOil;
    }

    public int getSugar() {
        return sugar;
    }
}
