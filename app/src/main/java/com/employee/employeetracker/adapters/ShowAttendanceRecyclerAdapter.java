package com.employee.employeetracker.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.employee.employeetracker.MapsActivity;
import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Employee;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


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

        final String getAdapterPosition = getRef(position).getKey();

        holder.checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("position", getAdapterPosition);
                intent.putExtra("name", model.getUserName());
                intent.putExtra("dutyPost", model.getDutyPost());
                intent.putExtra("shift", model.getTypeOfShift());
                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }
        });
    }


    @NonNull
    @Override
    public ShowAttendanceAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        return new ShowAttendanceAdapter(LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.layout_view_employee_check_in, viewGroup, false));

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


        ShowAttendanceAdapter(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            //txtViewCheckOut = view.findViewById(R.id.txtViewCheckOutUsers);
            txtName = view.findViewById(R.id.txttName);
            txtViewCheckIn = view.findViewById(R.id.txttCheckInTime);
            txtViewCheckOut = view.findViewById(R.id.txttCheckOutTime);
            txtDutyPost = view.findViewById(R.id.txttDutyPost);
            txtShift = view.findViewById(R.id.txttShift);
            checkOut = view.findViewById(R.id.btnnCheckOut);

        }

        void showCheckInEmployeeName(String name) {
            // TextView nameOfAttendee = view.findViewById(R.id.txtCheckInNameOfEmployee);
            // nameOfAttendee.setText(name);
            txtName.setText(name);
        }

        public void showDate(String date) {
            //  TextView nameOfAttendee = view.findViewById(R.id.txtCheckOutNameOfEmployee);
            // nameOfAttendee.setText(name);

            try {
                txtViewCheckIn.setText(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void showCheckInDate(long date) {

            // TextView txtCheckInDate = view.findViewById(R.id.textViewShowCheckInTimeStamp);
            //  SimpleDateFormat sfd = new SimpleDateFormat("EEEE ' ' dd-MMMM-yyyy '@' hh:mm aa", Locale.US);

//            try {
//                txtViewCheckIn.setText(String.format("Checked in on : %s", GetDateTime.getFormattedDate(new Date(date))));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }

        public void showCheckOutDate(String date) {

            // TextView txtCheckOutDate = view.findViewById(R.id.textViewShowCheckOutTimeStamp);
            //  SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy '@' hh:mm aa", Locale.US);

            try {
                if (date.isEmpty()) {
                    txtViewCheckOut.setText(" ");
                } else
                    txtViewCheckOut.setText(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void showDutyPost(String name) {
            txtDutyPost.setText(name);
            //  TextView nameOfAttendee = view.findViewById(R.id.txtCheckOutNameOfEmployee);
            // nameOfAttendee.setText(name);
        }

        public void showShift(String name) {
            txtShift.setText(name);
            //  TextView nameOfAttendee = view.findViewById(R.id.txtCheckOutNameOfEmployee);
            // nameOfAttendee.setText(name);
        }

    }
}


