package co.astrnt.medrec.medrec.tesst;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import co.astrnt.medrec.medrec.R;

/**
 * Created by hill on 7/21/17.
 */

public class RangeBarThumbAdapter extends RecyclerView.Adapter<RangeBarThumbAdapter.TVH> {
    int capacity;
    int width;

    int height;
    int perSeconds;
    String videoPath;
    private MediaMetadataRetriever mediaMetadataRetriever;
    Context mContext;

    public RangeBarThumbAdapter(Context mContext, String videoPath, int capacity, int width, int height, int perSeconds) {
        this.mContext = mContext;

        this.capacity = capacity;
        this.width = width;
        this.videoPath = videoPath;
        this.height = height;
        this.perSeconds = perSeconds;

        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
    }


    @Override
    public TVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.trim_image, null);

        TVH x = new TVH(v);

        return x;
    }

    @Override
    public void onBindViewHolder(TVH holder, int position) {
        Bitmap q = mediaMetadataRetriever.getFrameAtTime(position * perSeconds * 1000);
        Log.e("XTR", "" + q.getWidth() + " " + q.getHeight());
        Bitmap y = Bitmap.createScaledBitmap(q, width, height, true);
        q.recycle();
        ((ImageView)holder.itemView.findViewById(R.id.img)).setImageBitmap(y);

    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return capacity;
    }


    class TVH extends RecyclerView.ViewHolder {
        View itemView;

        public TVH(View itemView) {
            super(itemView);

            this.itemView = itemView;
        }
    }

}
