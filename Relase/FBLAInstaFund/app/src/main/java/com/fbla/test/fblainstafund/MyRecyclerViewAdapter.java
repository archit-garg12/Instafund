package com.fbla.test.fblainstafund;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import com.squareup.picasso.Picasso;

import java.util.List;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;


/**
 * Created by root on 07/02/17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder>{
    private List<FeedItem> feedItemList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public static final int COMMENT = 1;
    public static final int BUY = 2;
    public static final int DELETE = 3;

    public MyRecyclerViewAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {
        final FeedItem feedItem = feedItemList.get(i);

        //Download image using picasso library
        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
//            Picasso.with(mContext).load(feedItem.getThumbnail())
//                    .error(R.drawable.placeholder)
//                    .placeholder(R.drawable.placeholder)
//                    .into(customViewHolder.imageView);
        }

        //Setting text view title
//        Log.d("MyRecycler", "Encoded String here :" + feedItem.getThumbnail() );

        customViewHolder.imageView.setImageBitmap(decodeFromBase64ToBitmap(feedItem.getThumbnail()));
        customViewHolder.textViewItemName.setText(Html.fromHtml(feedItem.getItemName()));
        customViewHolder.textViewItemPrice.setText(Html.fromHtml(feedItem.getItemPrice()));
        customViewHolder.textViewItemDesc.setText(Html.fromHtml(feedItem.getItemDescription()));
        customViewHolder.textViewItemState.setText(Html.fromHtml(feedItem.getItemState()));

        // Compare with User ID saved in Application

        SharedPreferences sp = mContext.getSharedPreferences("com.whiznets.test.fblainstafund",Activity.MODE_PRIVATE);
                //this.getPreferences(Activity.MODE_PRIVATE);
        String id = sp.getString("user_id","");

        if(!feedItem.getItemUserID().equals(id)){
            customViewHolder.imgButtonDelete.setVisibility(View.INVISIBLE);
        }

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.comment ) {
                    onItemClickListener.onItemClick(feedItem, COMMENT);
                } else if(v.getId() == R.id.buy){
                    onItemClickListener.onItemClick(feedItem, BUY);
                } else if(v.getId() == R.id.delete){
//                    removeAt(i);
                    onItemClickListener.onItemClick(feedItem, DELETE);
                }

            }
        };
//        customViewHolder.imageView.setOnClickListener(listener);
//        customViewHolder.textViewItemName.setOnClickListener(listener);
        customViewHolder.imgButtonDelete.setOnClickListener(listener);
        customViewHolder.buttonComment.setOnClickListener(listener);
        customViewHolder.buttonBuy.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textViewItemName;
        protected TextView textViewItemPrice;
        protected TextView textViewItemState;
        protected TextView textViewItemDesc;
        protected Button buttonComment;
        protected Button buttonBuy;
        protected ImageButton imgButtonDelete;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textViewItemName = (TextView) view.findViewById(R.id.title);
            this.textViewItemPrice = (TextView) view.findViewById(R.id.itemPrice);
            this.textViewItemState = (TextView) view.findViewById(R.id.itemState);
            this.textViewItemDesc = (TextView) view.findViewById(R.id.itemDesc);
            this.buttonBuy = (Button) view.findViewById(R.id.comment);
            this.buttonComment = (Button) view.findViewById(R.id.buy);
            this.imgButtonDelete = (ImageButton) view.findViewById(R.id.delete);
        }
    }


    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAt(int position) {
        feedItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, feedItemList.size());
    }

    /**
     *  Decode Encoded Image in Base64 to BitMap for use.
     * @param encodedImage
     * @return
     */
    private Bitmap decodeFromBase64ToBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
