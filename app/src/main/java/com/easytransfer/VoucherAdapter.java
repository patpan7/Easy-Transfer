package com.easytransfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VoucherAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<Voucher> originalList;
    private List<Voucher> filteredList;
    private VoucherFilter filter;

    public VoucherAdapter(Context context, List<Voucher> list) {
        this.context = context;
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
    }

    @Override
    public int getCount() { return filteredList.size(); }

    @Override
    public Object getItem(int i) { return filteredList.get(i); }

    @Override
    public long getItemId(int i) { return filteredList.get(i).getId(); }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Voucher v = filteredList.get(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.voucher_list_item, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDate = convertView.findViewById(R.id.tvDate);

        tvName.setText(v.getName());
        tvDate.setText("Ημερομηνία: " + v.getDate());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new VoucherFilter();
        }
        return filter;
    }

    private class VoucherFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = originalList;
                results.count = originalList.size();
            } else {
                List<Voucher> filtered = new ArrayList<>();
                for (Voucher v : originalList) {
                    if (v.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            v.getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filtered.add(v);
                    }
                }

                results.values = filtered;
                results.count = filtered.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<Voucher>) results.values;
            notifyDataSetChanged();
        }
    }
}
