package com.employee.employeetracker.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Report;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends FirebaseRecyclerAdapter<Report, CommentsAdapter.ShowCommentsHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentsAdapter(@NonNull FirebaseRecyclerOptions<Report> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowCommentsHolder holder, int position, @NonNull Report model) {
        holder.showUserPhoto(model.getImage());
        holder.showUserName(model.getFullName());
        holder.showDate(model.getTimeStamp());
        holder.showComment(model.getComments());
    }

    @NonNull
    @Override
    public ShowCommentsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowCommentsHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_comment_list_items, viewGroup, false));
    }

    public class ShowCommentsHolder extends RecyclerView.ViewHolder {
        View view;

        ShowCommentsHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;


        }

        //display the user name of the person who posted
        void showUserName(String name) {
            TextView nameOfUser = view.findViewById(R.id.comment_username);
            nameOfUser.setText(name);
        }

        //display the content
        void showComment(String description) {
            TextView crimeDescription = view.findViewById(R.id.comment_message);
            crimeDescription.setText(description);
        }


        //display the users photo
        void showUserPhoto(String urlOfUserPhoto) {
            CircleImageView userProfilePhoto = view.findViewById(R.id.comment_image);
            Glide.with(view).load(urlOfUserPhoto).into(userProfilePhoto);
        }


        void showDate(Long date) {

            TextView time = view.findViewById(R.id.commentDate);
            SimpleDateFormat sfd = new SimpleDateFormat("EEE dd-MMMM-yyyy '@' hh:mm aa ",
                    Locale.US);

            try {
                time.setText(sfd.format((new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
