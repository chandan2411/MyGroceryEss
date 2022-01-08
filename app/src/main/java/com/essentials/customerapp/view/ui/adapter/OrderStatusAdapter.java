package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.models.OrderStatusModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.view.ui.activity.ItemsOrderedActivity;

import java.util.List;

/**
 * Created by Raj Aryan on 7/12/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolder> {
    Context mContext;
    List<OrderStatusModel> statusModelList;

    public OrderStatusAdapter(Context context, List<OrderStatusModel> statusModelList) {
        mContext = context;
        this.statusModelList = statusModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_order_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "OrderStatusAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderStatusModel model = statusModelList.get(position);
        if (position==0){
            holder.lineStatus.setVisibility(View.GONE);
        }else {
            holder.lineStatus.setVisibility(View.VISIBLE);
        }
        String formattedDate = model.getDateTime();
        try{
            formattedDate = AppUtils.formatDateToFrom(model.getDateTime(), "yyyy-MM-dd hh:mm:ss a", "dd MMMM yyyy hh:mm:ss a");
        }catch (Exception ex){
            Log.e(TAG, "onBindViewHolder: "+ex.getMessage(), ex);
        }
        holder.tvOrderStatusText.setText(model.getOrderStatus());
        holder.tvOrderStatusDateTIme.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return statusModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View lineStatus;
        TextView tvOrderStatusText, tvOrderStatusDateTIme;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lineStatus = itemView.findViewById(R.id.lineStatus);
            tvOrderStatusText = itemView.findViewById(R.id.tvOrderStatusText);
            tvOrderStatusDateTIme = itemView.findViewById(R.id.tvOrderStatusDateTIme);
        }
    }
}
