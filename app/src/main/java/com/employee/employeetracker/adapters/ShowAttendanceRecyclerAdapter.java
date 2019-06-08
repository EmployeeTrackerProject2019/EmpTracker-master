package com.employee.employeetracker.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.MapsActivity;
import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Employee;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShowAttendanceRecyclerAdapter extends FirebaseRecyclerAdapter<Employee, ShowAttendanceRecyclerAdapter.ShowAttendanceAdapter> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ShowAttendanceRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Employee> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowAttendanceAdapter holder, int position, @NonNull final Employee model) {
        holder.showCheckInEmployeeName(model.getUserName());
        holder.showDate(model.getDate());
        holder.showCheckOutDate(model.getCheckOutTimeStamp());
        holder.showDutyPost(model.getDutyPost());
        holder.showShift(model.getTypeOfShift());
        // holder.showPhoto(model.getCheckInPhoto());

        final String getAdapterPosition = getRef(position).getKey();

        holder.checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("position", getAdapterPosition);
                intent.putExtra("name", model.getUserName());
                intent.putExtra("dutyPost", model.getDutyPost());
                intent.putExtra("shift", model.getTypeOfShift());
                intent.putExtra("photo", model.getCheckInPhoto());
                intent.putExtra("checkInTime", model.getDate());
                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }
        });
    }


    @NonNull
    @Override
    public ShowAttendanceAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowAttendanceAdapter(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_test_combine, viewGroup, false));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ShowAttendanceAdapter extends RecyclerView.ViewHolder {
        View view;
        TextView txtName, txtViewCheckIn, txtViewCheckOut, txtDutyPost, txtShift;
        Button checkOut;
        CircleImageView userPhoto;


        ShowAttendanceAdapter(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            txtName = view.findViewById(R.id.txttName);
            txtViewCheckIn = view.findViewById(R.id.txttCheckInTime);
            txtViewCheckOut = view.findViewById(R.id.txttCheckOutTime);
            txtDutyPost = view.findViewById(R.id.txttDutyPost);
            txtShift = view.findViewById(R.id.txttShift);
            checkOut = view.findViewById(R.id.btnnCheckOut);
            userPhoto = view.findViewById(R.id.checkInPhoto);

        }

        void showCheckInEmployeeName(String name) {
            txtName.setText(name);
        }

        void showDate(String date) {
            try {
                txtViewCheckIn.setText(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void showPhoto(String url) {
            Glide.with(view).load(url).into(userPhoto);

        }

        void showCheckOutDate(String date) {
            try {
                if (date.isEmpty()) {
                    txtViewCheckOut.setText(" ");
                } else
                    txtViewCheckOut.setText(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        void showDutyPost(String name) {
            txtDutyPost.setText(name);

        }

        void showShift(String name) {
            txtShift.setText(name);

        }

    }
}


