package com.employee.employeetracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.employee.employeetracker.R;

public class SlidePagerAdapter extends PagerAdapter {

    //Array of type String to display the descriptions that moves on the screen
    public final String[] slideDescriptions = {"Dummy Txt1", "Dummy Txt2", "Dummy Txt3", "Dummy Txt4", "Dummy Txt5", "Dummy Txt5"};
    //object of the Context class to allow views to be passed into another activity
    private final Context context;
    //Array of type int to display the descriptions that moves on the screen
    private int[] slideImages = {R.drawable.b, R.drawable.bb, R.drawable.bbb, R.drawable.bbbb, R.drawable.bbbbb, R.drawable.bbbbbb};

    //Constructor to initialize objects
    public SlidePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return slideImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.layout_slide_page_items, container, false);

        ImageView imageView = view.findViewById(R.id.slideImage);
        TextView TV1 = view.findViewById(R.id.txtWelcomeDes);

        imageView.setImageResource(slideImages[position]);
        TV1.setText(slideDescriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);

    }
}
