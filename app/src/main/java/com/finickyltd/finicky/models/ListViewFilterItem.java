package com.finickyltd.finicky.models;

import android.graphics.drawable.Drawable;

public class ListViewFilterItem {

    private Drawable iconExpandCollapse;
    private String filterName;
    private String filterIngredient;
    private int bgColor;
    private boolean visible;

    public void setIconExpandCollapse(Drawable icon){this.iconExpandCollapse = icon; }

    public void setFilterName(String filterName){ this.filterName = filterName; }

    public void setFilterIngredient(String filterIngredient){this.filterIngredient = filterIngredient;   }

    public void setBgColor(int bgColor) {this.bgColor = bgColor;  }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Drawable getIconExpandCollapse(){
        return this.iconExpandCollapse;
    }

    public String getFilterName(){
        return this.filterName;
    }

    public String getFilterIngredient(){ return this.filterIngredient; }

    public int getBgColor() {return bgColor; }

    public boolean getVisible() {return visible; }
}
