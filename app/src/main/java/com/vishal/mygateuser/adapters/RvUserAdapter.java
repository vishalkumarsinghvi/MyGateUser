package com.vishal.mygateuser.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vishal.mygateuser.R;
import com.vishal.mygateuser.models.User;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RvUserAdapter extends RecyclerView.Adapter<RvUserAdapter.ViewHolder> {
    private ArrayList<User> userData;
    private Context context;

    public RvUserAdapter(ArrayList<User> userData, Context context) {
        this.userData = userData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(userData.get(position).getUserName());
        holder.tvPassCode.setText(userData.get(position).getPassCode());
//        Glide.with(context).load().into(holder.cvImageView);
        File imgFile = new File(userData.get(position).getImgUrl());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.cvImageView.setImageBitmap(myBitmap);

        }
    }


    @Override
    public int getItemCount() {
        return userData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cvImageView;
        TextView tvUserName;
        TextView tvPassCode;

        ViewHolder(View itemView) {
            super(itemView);
            this.cvImageView = itemView.findViewById(R.id.profile_image);
            this.tvUserName = itemView.findViewById(R.id.tv_user_name);
            this.tvPassCode = itemView.findViewById(R.id.tv_pass_code);
        }

    }
}