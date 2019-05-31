package com.employee.employeetracker.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Employee;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
    protected void onBindViewHolder(@NonNull ShowAttendanceAdapter holder, int position, @NonNull Employee model) {
        holder.showCheckInEmployeeName(model.getUserName());
        holder.showCheckInDate(model.getCheckInTimeStamp());
    }


    @NonNull
    @Override
    public ShowAttendanceAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowAttendanceAdapter(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_view_employee_check_in, viewGroup, false));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ShowAttendanceAdapter extends RecyclerView.ViewHolder {
        View view;
        TextView txtViewCheckIn, txtViewCheckOut;


        ShowAttendanceAdapter(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            txtViewCheckOut = view.findViewById(R.id.txtViewCheckOutUsers);


        }

        void showCheckInEmployeeName(String name) {
            TextView nameOfAttendee = view.findViewById(R.id.txtCheckInNameOfEmployee);
            nameOfAttendee.setText(name);
        }

        public void showCheckOutEmployeeName(String name) {
            TextView nameOfAttendee = view.findViewById(R.id.txtCheckOutNameOfEmployee);
            nameOfAttendee.setText(name);
        }

        void showCheckInDate(long date) {

            TextView txtCheckInDate = view.findViewById(R.id.textViewShowCheckInTimeStamp);
            SimpleDateFormat sfd = new SimpleDateFormat("EEEE ' ' dd-MMMM-yyyy '@' hh:mm aa", Locale.US);

            try {
                txtCheckInDate.setText(String.format("Checked in on : %s", sfd.format(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void showCheckOutDate(long date) {

            TextView txtCheckOutDate = view.findViewById(R.id.textViewShowCheckOutTimeStamp);
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy '@' hh:mm aa", Locale.US);

            try {
                txtCheckOutDate.setText(String.format("Checked out on : %s",
                        sfd.format(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}


