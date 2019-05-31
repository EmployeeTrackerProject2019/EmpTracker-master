package com.employee.employeetracker.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Leave;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaveAdapterViewHolder extends FirebaseRecyclerAdapter<Leave,
        LeaveAdapterViewHolder.ShowLeaveViewHolder> {


    /**
     * Initialize a {@link android.support.v7.widget.RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link com.firebase.ui.database.FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LeaveAdapterViewHolder(@NonNull FirebaseRecyclerOptions<Leave> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowLeaveViewHolder holder, int position, @NonNull Leave model) {

        String getAdapterPostion = getRef(position).getKey();

        holder.showUserPhoto(model.getImage());
        holder.showDate(model.getTimeStamp());
        holder.showResponse(model.getLeaveResponse());


        holder.btnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @NonNull
    @Override
    public ShowLeaveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowLeaveViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_view_leave, viewGroup, false));
    }

    //an inner class to hold the views to be inflated
    public class ShowLeaveViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private Button btnView;


        ShowLeaveViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnView = view.findViewById(R.id.btnView);
        }


        void showDate(Long date) {

            TextView txtDate = view.findViewById(R.id.txtShowLeaveDate);
            SimpleDateFormat sfd = new SimpleDateFormat("'Sent on ' dd-MM-yyyy '@' hh:mm aa",
                    Locale.US);

            try {
                txtDate.setText(sfd.format(new Date(date)));
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
            txtResponse.setText(response);
        }


    }
}
