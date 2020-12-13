package com.employee.employeetracker.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.CommentsActivity;
import com.employee.employeetracker.activities.EditReportActivity;
import com.employee.employeetracker.adapters.ReportViewHolderAdapter.ShowReportViewHolder;
import com.employee.employeetracker.models.Report;
import com.employee.employeetracker.utils.GetDateTime;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportViewHolderAdapter extends FirebaseRecyclerAdapter<Report, ShowReportViewHolder> {
    private Intent intent;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link com.firebase.ui.database.FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ReportViewHolderAdapter(@NonNull FirebaseRecyclerOptions<Report> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ShowReportViewHolder holder, int position, @NonNull final Report model) {
        holder.showReportTitle(model.getTitle());
        holder.showDateReported(model.getTimeStamp());
        holder.showReportDescription(model.getDescription());
        holder.showUserPhoto(model.getImage());
        holder.showUserName(model.getFullName());

        final String getAdapterPosition = getRef(position).getKey();


//passes the data to edit report activity using the holder of the image
        holder.imgEditReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(v.getContext(), EditReportActivity.class);
                intent.putExtra("position", getAdapterPosition);
                intent.putExtra("title", model.getTitle());
                intent.putExtra("image", model.getImage());
                intent.putExtra("description", model.getDescription());

                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });

        //passes the data to comment activity using the holder of the text view
        holder.txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), CommentsActivity.class);
                intent.putExtra("position", getAdapterPosition);
                intent.putExtra("image", model.getImage());
                intent.putExtra("name", model.getFullName());
                intent.putExtra("description", model.getDescription());
                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


    }

    @NonNull
    @Override
    public ShowReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowReportViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_show_report, viewGroup, false
        ));
    }

    //an inner class to hold the views to be inflated
    public class ShowReportViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageButton imgEditReport;
        TextView txtComment;


        ShowReportViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imgEditReport = view.findViewById(R.id.imgEditReport);
            txtComment = view.findViewById(R.id.txtComment);
        }


        //display the report title
        void showReportTitle(String reportTitle) {
            TextView txtReportTitle = view.findViewById(R.id.txtShowReportTitle);
            txtReportTitle.setText(reportTitle);
        }


        void showDateReported(Long date) {

            TextView txtReportDate = view.findViewById(R.id.txtShowReportDate);
            // SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd-MM-yyyy '@' hh:mm aa", Locale.US);

            try {
                txtReportDate.setText(String.format("Sent a report on : %s",
                        GetDateTime.getFormattedDate(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //display the report description
        void showReportDescription(String reportDescription) {
            TextView txtReportDescription = view.findViewById(R.id.txtShowReportDescription);
            txtReportDescription.setText(reportDescription);
        }


        //display the user photo
        void showUserPhoto(String urlOfImage) {
            CircleImageView checkInPhoto = view.findViewById(R.id.imageViewShowReportPhoto);

            Glide.with(view).load(urlOfImage).into(checkInPhoto);
        }


        //display the user name of the person who checked in
        void showUserName(String name) {
            TextView nameOfUser = view.findViewById(R.id.txtShowNameOfUser);
            nameOfUser.setText(name);
        }


    }

}
