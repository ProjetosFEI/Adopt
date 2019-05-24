package com.example.adoptapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mMatchName, mMatchPhone; //mMatchId
    public ImageView mMatchImage;
    public MatchesViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);

        //mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchPhone = (TextView) itemView.findViewById(R.id.MatchPhone);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View view){

    }
}
