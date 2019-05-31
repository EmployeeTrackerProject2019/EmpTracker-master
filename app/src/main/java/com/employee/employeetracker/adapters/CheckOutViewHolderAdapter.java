package com.employee.employeetracker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.CheckOutViewHolderAdapter.ShowCheckOutAttendanceViewHolder;
import com.employee.employeetracker.bottomsheets.CheckOutDetailsBottomSheet;
import com.employee.employeetracker.models.Employee;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckOutViewHolderAdapter extends FirebaseRecyclerAdapter<Employee,
        ShowCheckOutAttendanceViewHolder> {
    private Context context;


    /**
     * Initialize a {@link android.support.v7.widget.RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link com.firebase.ui.database.FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CheckOutViewHolderAdapter(@NonNull FirebaseRecyclerOptions<Employee> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull ShowCheckOutAttendanceViewHolder holder, int position, @NonNull Employee model) {

        holder.showCheckOutDate(model.getCheckOutTimeStamp());
//        holder.showCheckOutPhoto(model.getCheckOutPhoto());
//        holder.showDutyPost(model.getDutyPost());
//        holder.showUserName(model.getUserName());
//        holder.showWorkShift(model.getTypeOfShift());

        //get the post position using the positions in each view holder
        final String getPostPosition = getRef(position).getKey();

        holder.txtViewMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the values to the bottom sheet
                final Bundle bundle = new Bundle();
                bundle.putString("position", getPostPosition);
                // loading.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // loading.setVisibility(View.GONE);
                        Fragment manager = new Fragment();
                        CheckOutDetailsBottomSheet checkOutDetailsBottomSheet =
                                new CheckOutDetailsBottomSheet();
                        checkOutDetailsBottomSheet.setArguments(bundle);
                        assert manager.getFragmentManager() != null;
                        checkOutDetailsBottomSheet.show(manager.getFragmentManager(), "checkout");

                    }
                }, 3000);
            }
        });

    }

    @NonNull
    @Override
    public ShowCheckOutAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowCheckOutAttendanceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_show_check_out, viewGroup, false));
    }

    //Method to delete value in recycler
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }


    //an inner class to hold the views to be inflated
    public class ShowCheckOutAttendanceViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtViewMore;


        ShowCheckOutAttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtViewMore = view.findViewById(R.id.txtViewCheckOutDetails);
        }


        //display the user name of the person who checked in
        void showUserName(String name) {
            TextView nameOfAttendee = view.findViewById(R.id.textViewCheckOutName);
            nameOfAttendee.setText(name);
        }


        void showCheckOutDate(Long date) {

            TextView txtCheckOutDate = view.findViewById(R.id.txtShowCheckOutTime);
            SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd-MMMM-yyyy '@' hh:mm aa",
                    Locale.US);

            try {
                txtCheckOutDate.setText(String.format("You checked out on \n %s",
                        sfd.format(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //display the check in photo
        void showCheckOutPhoto(String urlOfImage) {
            CircleImageView checkOutPhoto = view.findViewById(R.id.imageViewCheckOut);

            Glide.with(view)
                    .load(urlOfImage)
                    .into(checkOutPhoto);
        }


        //display the duty post
        void showDutyPost(String dutyPost) {
            TextView txtDutyPost = view.findViewById(R.id.textViewDutyPost);
            txtDutyPost.setText(dutyPost);
        }

        //display the work shift
        void showWorkShift(String workShift) {
            TextView txtWorkShift = view.findViewById(R.id.textViewWorkShift);
            txtWorkShift.setText(workShift);
        }

        //display the location of the user
        void showLocation(String location) {
            TextView txtLocation = view.findViewById(R.id.txtViewAttendeeLocation);
            txtLocation.setText(location);
        }


    }

}

