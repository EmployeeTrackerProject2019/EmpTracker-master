package com.employee.employeetracker.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Leave;
import com.employee.employeetracker.utils.GetDateTime;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaveAdapterViewHolder extends FirebaseRecyclerAdapter<Leave,
        LeaveAdapterViewHolder.ShowLeaveViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link com.firebase.ui.database.FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LeaveAdapterViewHolder(@NonNull FirebaseRecyclerOptions<Leave> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowLeaveViewHolder holder, int position, @NonNull final Leave model) {

        String getAdapterPostion = getRef(position).getKey();

        holder.showUserPhoto(model.getImage());
        holder.showDate(model.getTimeStamp());
        holder.showResponse(model.getLeaveResponse());


        holder.btnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setTitle(model.getLeaveResponse())
                        .setIcon(v.getContext().getResources().getDrawable(R.drawable.sorry))
                        .setMessage(model.getLeaveMsg())
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();


                            }
                        }).create().show();


            }
        });

    }

    @NonNull
    @Override
    public ShowLeaveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowLeaveViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_view_leave, viewGroup, false));
    }

    //an inner class to hold the views to be inflated
    public static class ShowLeaveViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final Button btnView;


        ShowLeaveViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnView = view.findViewById(R.id.btnView);
        }


        void showDate(Long date) {

            TextView txtDate = view.findViewById(R.id.txtShowLeaveDate);
//            SimpleDateFormat sfd = new SimpleDateFormat("'Sent on ' dd-MM-yyyy '@' hh:mm aa",
//                    Locale.US);

            try {
                txtDate.setText(GetDateTime.getFormattedDate(new Date(date)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //display the user photo
        void showUserPhoto(String urlOfImage) {
            CircleImageView checkInPhoto = view.findViewById(R.id.imageViewShowLeavePhoto);

            Glide.with(view).load(urlOfImage).into(checkInPhoto);
        }


        //display the response
        void showResponse(String response) {
            TextView txtResponse = view.findViewById(R.id.txtResultsFromAdmin);
            if (response.equals("Leave approved")) {
                txtResponse.setTextColor(view.getContext().getResources().getColor(R.color.colorGreen));
            } else if (response.equals("Leave rejected")) {
                txtResponse.setTextColor(view.getContext().getResources().getColor(R.color.colorRed));
            }

            txtResponse.setText(response);
        }


    }
}
