package com.example.adoptapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mMatchName, mMatchPhone, mMatchId;
    public ImageView mMatchImage, mCallMatch;
    public MatchesViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchPhone = (TextView) itemView.findViewById(R.id.MatchPhone);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
        mCallMatch = (ImageView) itemView.findViewById(R.id.callMatch);

        mCallMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                return;
            }
        });
    }

    @Override
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);

    }
}
