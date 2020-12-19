package com.employee.employeetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Employee;

import java.util.ArrayList;

public class EmployeeDutyAdapter extends RecyclerView.Adapter<EmployeeDutyAdapter.ShowDutyViewHolder> {

    private final Context context;
    private final ArrayList<Employee> arrayList;

    public EmployeeDutyAdapter(Context context, ArrayList<Employee> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ShowDutyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_duty,
                viewGroup,
                false);

        return new ShowDutyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowDutyViewHolder showDutyViewHolder, int i) {

        ShowDutyViewHolder rowViewHolder;
        rowViewHolder = showDutyViewHolder;

        int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtName.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtDuty.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtShift.setBackgroundResource(R.drawable.table_header_cell_bg);

            rowViewHolder.txtShift.setTextColor(context.getResources().getColor(R.color.white));
            rowViewHolder.txtName.setTextColor(context.getResources().getColor(R.color.white));
            rowViewHolder.txtDuty.setTextColor(context.getResources().getColor(R.color.white));


            rowViewHolder.txtName.setText(context.getResources().getString(R.string.nameOfPerson));
            rowViewHolder.txtDuty.setText(context.getResources().getString(R.string.duty_Post));
            rowViewHolder.txtShift.setText(context.getResources().getString(R.string.shtfType));

        } else {


            Employee attendance = arrayList.get(rowPos - 1);


            // Content Cells. Content appear here
            rowViewHolder.txtName.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtDuty.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtShift.setBackgroundResource(R.drawable.table_content_cell_bg);

            rowViewHolder.txtName.setText(attendance.getFullName());
            rowViewHolder.txtDuty.setText(attendance.getDutyPost());
            rowViewHolder.txtShift.setText(attendance.getTypeOfShift());
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//
//    }

    public static class ShowDutyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtDuty;
        private final TextView txtShift;
        private final TextView txtName;

        public ShowDutyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtShift = itemView.findViewById(R.id.txtShiftww);
            txtDuty = itemView.findViewById(R.id.txtPositionww);
            txtName = itemView.findViewById(R.id.txtNameww);

        }


    }
}
