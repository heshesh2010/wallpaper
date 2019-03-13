package com.wizy.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdView;
import com.view.jameson.library.CardAdapterHelper;
import com.wizy.wallpaper.FullscreenActivity;
import com.wizy.wallpaper.MainActivity;
import com.wizy.wallpaper.R;
import com.wizy.wallpaper.models.Results;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The banner ad view type.
    private static final int BANNER_AD_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context context;

    // The list of banner ads and menu items.
    private final List<Object> wallPaperImagesResults;

    private final CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();

    Intent  intent ;
    /**
     * For this example app, the wallPaperImagesResults  list contains only
     * {@link MenuItem} and {@link AdView} types.
     */
    public CardRecyclerViewAdapter(Context context, List<Object> wallPaperImagesResults) {
        this.context = context;
        this.wallPaperImagesResults = wallPaperImagesResults;
    }



    /**
     * The {@link MenuItemViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CircleImageView profileImage;
TextView userName;
        MenuItemViewHolder(View view) {
            super(view);
            this.image = itemView.findViewById(R.id.imageView);
this.profileImage = itemView.findViewById(R.id.profileImage);
this.userName= itemView.findViewById(R.id.userName);
        }
    }

    /**
     * The {@link AdViewHolder} class.
     */
    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return wallPaperImagesResults.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        return (position % MainActivity.ITEMS_PER_AD == 0) ?
                BANNER_AD_VIEW_TYPE
                : MENU_ITEM_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view or a banner ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.wallpaper_adapter, viewGroup, false);
                mCardAdapterHelper.onCreateViewHolder(viewGroup, menuItemLayoutView);
                return new MenuItemViewHolder(menuItemLayoutView);

            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                View bannerLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.banner_ad_container,
                        viewGroup, false);

                mCardAdapterHelper.onCreateViewHolder(viewGroup, bannerLayoutView);
                return new AdViewHolder(bannerLayoutView);
        }
    }

    /**
     * Replaces the content in the views that make up the menu item view and the
     * banner ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());

        switch (viewType) {
            case  MENU_ITEM_VIEW_TYPE:

                MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;

                setImage(menuItemHolder.image,position);

                // Profile Imageof image uploader user
                setProfileImage(menuItemHolder.profileImage,position);

                // user name of image uploader user
                menuItemHolder.userName.setText(((Results) wallPaperImagesResults.get(position)).getUser().getUsername());

                // when i click on image it get current object of wallpaper then open full screen activity
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), FullscreenActivity.class);
                    intent.putExtra("Result", (Results)wallPaperImagesResults.get(position));
                    view.getContext().startActivity(intent);
                });

                break;
            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                AdViewHolder bannerHolder = (AdViewHolder) holder;
                AdView adView = (AdView) wallPaperImagesResults.get(position);
                ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                // The AdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // AdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled AdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView);
        }
    }


    private void setImage(ImageView mImageView, int position) {

        Glide.with(context)
                .asBitmap()
                .load(((Results)wallPaperImagesResults.get(position)).getUrls().getFit())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });



    }



    private void setProfileImage(ImageView mImageView, int position) {

        Glide.with(context)
                .asBitmap()
                .load(((Results)wallPaperImagesResults.get(position)).getUser().getProfile_image().getLarge())
                .apply(new RequestOptions().circleCropTransform())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });



    }

}
