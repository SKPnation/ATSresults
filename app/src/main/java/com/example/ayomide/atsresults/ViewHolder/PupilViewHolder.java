package com.example.ayomide.atsresults.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ayomide.atsresults.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PupilViewHolder extends RecyclerView.ViewHolder
{
    public CircleImageView pupil_image;
    public TextView pupil_name;
    public Button btnReport, btnBill;

    public PupilViewHolder(@NonNull View itemView) {
        super( itemView );

        pupil_image = itemView.findViewById( R.id.pupil_image );
        pupil_name = itemView.findViewById( R.id.pupil_name );

        btnReport = itemView.findViewById( R.id.btnReport );
        btnBill = itemView.findViewById( R.id.btnBill );
    }

}
