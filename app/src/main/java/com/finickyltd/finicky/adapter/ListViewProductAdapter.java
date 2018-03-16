package com.finickyltd.finicky.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.models.ListViewProductItem;
import com.finickyltd.finicky.R;

public class ListViewProductAdapter extends BaseAdapter {

    private ArrayList<ListViewProductItem> listViewProductItems = new ArrayList<ListViewProductItem>();
    private DisplayImageOptions options;
    public ListViewProductAdapter(){

    }

    public ListViewProductAdapter(Context context) {
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public int getCount() {
        return listViewProductItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_item, parent, false);
        }

        TextView tvProductName = (TextView) convertView.findViewById(R.id.tvProductName) ;
        LinearLayout llSearchItemContainer = (LinearLayout) convertView.findViewById(R.id.llSearchItemContainer);
        final ImageView productImage = (ImageView) convertView.findViewById(R.id.imgProductView);
        TextView tvSuitableType = (TextView) convertView.findViewById(R.id.txtSuitableView);
        ListViewProductItem listViewItem = listViewProductItems.get(position);

        tvProductName.setText(listViewItem.getProductName());
        tvSuitableType.setText(listViewItem.getSuitableType());

        switch (listViewItem.getSuitableColor()){
            case 0:   // not suitable
                tvSuitableType.setTextColor(Color.rgb(254, 0, 0));
                break;
            case 1: // maybe suitable
                tvSuitableType.setTextColor(Color.rgb(255, 118, 0));
                break;
            case 2: // suitable
                tvSuitableType.setTextColor(Color.rgb(58, 219, 21));
                break;
        }

        ImageLoader.getInstance().displayImage(listViewItem.getProductImageUrl(), productImage, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                productImage.setImageResource(R.drawable.loading);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                productImage.setImageResource(R.drawable.image_not_available);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d("ImageLoad", "loading is LoadingComplete");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                productImage.setImageResource(R.drawable.image_not_available);
            }
        });

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewProductItems.get(position);
    }

    public void addItem(int cnt, String productName, String barcodeContent, int suitableType,String productSource){
        ListViewProductItem item = new ListViewProductItem();
        item.setProductName(productName);
        item.setBarcodeContent(barcodeContent);

        switch (suitableType){
            case 0:
                item.setSuitableType("NOT SUITABLE");
                item.setSuitableColor(0);
                break;
            case 1:
                item.setSuitableType("MAYBE SUITABLE");
                item.setSuitableColor(1);
                break;
            case 2:
                item.setSuitableType("SUITABLE");
                item.setSuitableColor(2);
                break;
        }

        // to load product image to ImageView
        String image_url = APIUrl.ROOT_URL;
        switch (productSource){
            case "sainsburys":
                image_url = image_url + APIUrl.SAINSBURYS_URL + barcodeContent + APIUrl.SAINS_SUFFIX;
                break;
            case "asda":
                image_url = image_url + APIUrl.ASDA_URL + barcodeContent + APIUrl.ASDA_SUFFIX;
                break;
            case "tesco":
                image_url = image_url + APIUrl.TESCO_URL + barcodeContent + APIUrl.TESCO_SUFFIX;
                break;
        }

        item.setProductImageUrl(image_url);

        listViewProductItems.add(item);
    }

    public void clearData(){
        listViewProductItems.clear();
    }
}
