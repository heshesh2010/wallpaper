package com.wizy.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizy.wallpaper.R;
import com.wizy.wallpaper.Search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public abstract class ChipAdapter  extends RecyclerView.Adapter<ChipAdapter.MyChipAdapter>{
    private final List<String> chips;
    private Drawable[] chipIcons ;
    private Context mContext;
    protected ChipAdapter(Context context, List<String> chips, Drawable[] chipIcons){
        mContext=context;
        this.chips=chips;
        this.chipIcons=chipIcons;

    }

    @NonNull
    @Override
    public MyChipAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chip_adapter, viewGroup, false);
        return new MyChipAdapter(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyChipAdapter myChipAdapter, int position) {

   //     myChipAdapter.chipName.setText(chips.get(position));
       /* String h =  new ArrayList<String>(chipIcons.values()).get(position);
        int m =  new ArrayList<Integer>(chipIcons.keySet()).get(position);*/

      //  myChipAdapter.button.setBackgroundResource(m);

        myChipAdapter.button.setBackground(chipIcons[position]);

       /* Drawable sampleDrawable = mContext.getResources().getDrawable(R.drawable.my_layer_list);
        sampleDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(h), PorterDuff.Mode.SRC_ATOP));
        myChipAdapter.button.setImageDrawable(sampleDrawable);*/


    }

    @Override
    public int getItemCount() {
        return chips.size();
    }

    class MyChipAdapter extends RecyclerView.ViewHolder{
        ImageButton button;
        TextView chipName;
        MyChipAdapter(@NonNull View itemView) {
            super(itemView);

                button = itemView.findViewById(R.id.button);
                chipName= itemView.findViewById(R.id.chipName);

                button.setOnClickListener(v -> {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        if (chips.get(pos).contains("SEARCH")) {
                            Intent intent = new Intent(itemView.getContext(), Search.class);
                            itemView.getContext().startActivity(intent);
                        }
                        else {
                            button.setEnabled(false);
                            clickListener(chips.get(pos),button);
                        }
                        SharedPreferences.Editor editor =    itemView.getContext().getSharedPreferences("wallpaper", MODE_PRIVATE).edit();
                        editor.putString("chip", chips.get(pos));
                        editor.apply();
                        }

                });

        }
    }

    protected abstract void clickListener(String chip, ImageButton button);

}
