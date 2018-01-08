package com.app.skybarge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.skybarge.R;
import com.app.skybarge.interfaces.OnCustomItemClicListener;
import com.app.skybarge.models.ModelPunchIn;
import com.app.skybarge.models.ModelStock;

import java.util.ArrayList;

/**
 * Created by admin on 26-11-2015.
 */
public class AdapterPunchInList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ModelPunchIn> detail;
    Context mContext;
    OnCustomItemClicListener listener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public AdapterPunchInList(Context context, OnCustomItemClicListener lis, ArrayList<ModelPunchIn> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_punchin_list, parent, false);

            vh = new CustomViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;

    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            this.progressBar.getIndeterminateDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CustomViewHolder) {

            ModelPunchIn m1 = (ModelPunchIn) detail.get(position);

            ((CustomViewHolder) holder).text_feedbak_type.setText("Punch In Time : " +m1.getPunch_in_time());
            ((CustomViewHolder) holder).text_date.setText( "Punch In Date : " +m1.getPunch_date());





        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }


    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView text_dapartment_label, text_feedbak_type, text_status, text_date, text_message, text_messageteacher;
        CardView card_view;

        public CustomViewHolder(View view) {
            super(view);

            this.text_date = (TextView) view.findViewById(R.id.text_date);
            this.text_feedbak_type = (TextView) view.findViewById(R.id.text_feedbak_type);
            this.card_view = (CardView) view.findViewById(R.id.card_view);
        }
    }

    public void setFilter(ArrayList<ModelPunchIn> detailnew) {
        detail = new ArrayList<>();
        detail.addAll(detailnew);
        notifyDataSetChanged();
    }

    public ModelPunchIn getFilter(int i) {

        return detail.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        ModelPunchIn m1 = (ModelPunchIn) detail.get(position);
        if (detail.get(position).getRowType() == 1) {
            return VIEW_ITEM;
        } else if (detail.get(position).getRowType() == 2) {
            return VIEW_PROG;
        }
        return -1;
    }
}

