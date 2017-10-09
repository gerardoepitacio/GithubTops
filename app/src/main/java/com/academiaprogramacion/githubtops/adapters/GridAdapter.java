package com.academiaprogramacion.githubtops.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.academiaprogramacion.githubtops.R;
import com.academiaprogramacion.githubtops.models.LanguageGridItem;

import java.util.List;

/**
 * Main menu grid adapter
 */
public class GridAdapter extends BaseAdapter {
    private Context context;
    private  List<LanguageGridItem> data;

    /**
     * Constructor
     * @param context
     * @param data
     */
    public GridAdapter(Context context, List<LanguageGridItem> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public LanguageGridItem getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Generate each items view
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        v = layoutInflater.inflate(R.layout.grid_item, null);
        TextView textView = v.findViewById(R.id.tv_language_name);
        LanguageGridItem d = getItem(position);
        textView.setText(d.getLanguage());
        textView.setCompoundDrawablesWithIntrinsicBounds(null, d.getDrawableResource(), null, null);
        return v;
    }
}
