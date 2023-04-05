package com.example.stayfit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stayfit.R;
import com.example.stayfit.model.BlogModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.viewHolder>{
        ArrayList<BlogModel> list;
        Context context;


    public BlogAdapter(ArrayList<BlogModel> list,Context context){
            this.list=list;
            this.context=context;
            }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_blog_layout,parent,false);
            return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder,int position){
        BlogModel model=list.get(position);
        Picasso.get()
        .load(model.getImageUrl())
        .into(holder.AuthorImage);

        holder.AuthorName.setText(model.getAuthor());
        holder.Work.setText(model.getWork());
        holder.TvUploadDate.setText(model.getDateUploaded());
        holder.Work.setText(model.getWork());

        holder.BtnLike.setOnClickListener(view->onLikeClick(holder));
        holder.BtnShare.setOnClickListener(view->onShareClick(holder));

    }


    @Override
    public int getItemCount(){
        return list.size();
    }

    private void onShareClick(viewHolder holder) {
        // Create a Share Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Author : " +holder.AuthorName.getText().toString() +
                "\n" + holder.Work.getText().toString()  +
                        "\nTo read more content like this download the StayFit app \nLink : \"to be given\" " );

        // Launch the Share Dialog
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void onLikeClick(viewHolder holder) {

    }
    public static class viewHolder extends RecyclerView.ViewHolder {

        ImageView AuthorImage, BtnLike, BtnShare;
        TextView Work, AuthorName,  TvUploadDate; //BtnFollow,

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            AuthorImage = itemView.findViewById(R.id.author_profile_image);
            AuthorName = itemView.findViewById(R.id.author_name);
            Work = itemView.findViewById(R.id.tv_blog);
            BtnLike = itemView.findViewById(R.id.iv_like);
            BtnShare = itemView.findViewById(R.id.iv_share);
            //BtnFollow = itemView.findViewById(R.id.btnFollow);
            TvUploadDate = itemView.findViewById(R.id.tv_upload_date);
        }
    }
}

