package com.employee.employeetracker.interfaces;


import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.employee.employeetracker.fragments.CheckInFragment;
import com.employee.employeetracker.fragments.CheckInFragment.ShowAttendanceViewHolder;

public class RecyclerItemTouchHelperCheckIn extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListenerCheckIn listener;


    public RecyclerItemTouchHelperCheckIn(int dragDirs, int swipeDirs,
                                          RecyclerItemTouchHelperListenerCheckIn listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder != null) {
            final View foregroundView = ((ShowAttendanceViewHolder) viewHolder).viewForeground;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        final View foregroundView =
                ((CheckInFragment.ShowAttendanceViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        final View foregroundView =
                ((CheckInFragment.ShowAttendanceViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView =
                ((CheckInFragment.ShowAttendanceViewHolder) viewHolder).viewForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    public interface RecyclerItemTouchHelperListenerCheckIn {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
