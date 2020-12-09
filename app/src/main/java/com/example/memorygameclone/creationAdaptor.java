package com.example.memorygameclone;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorygameclone.models.BoardSize;

import java.util.List;

public class creationAdaptor extends RecyclerView.Adapter<creationAdaptor.ViewHolder> {

    private List<Uri> images;
    private Context mContext;
    private BoardSize boardSize;
    private ImageClickListener imageClickListener;

    private static final int MARGIN = 10;

    public creationAdaptor(List<Uri> images, Context mContext, BoardSize boardSize, ImageClickListener imageClickListener) {
        this.images = images;
        this.mContext = mContext;
        this.boardSize = boardSize;
        this.imageClickListener = imageClickListener;
    }

    interface ImageClickListener{
        void onClickListener();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int cardWidth = parent.getWidth() / boardSize.getWidth() - (2*MARGIN);
        int cardHeight = parent.getHeight() / boardSize.getHeight() - (2*MARGIN);
        int cardSideLength = Math.min(cardHeight, cardWidth);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_image, parent, false);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.findViewById(R.id.imgViewCard).getLayoutParams();
        layoutParams.width = cardSideLength;
        layoutParams.height = cardSideLength;
        layoutParams.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (images != null) {
            if (position < images.size()) {
                holder.bind(images.get(position));
            }else {
                holder.bind();
            }
        }
    }

    @Override
    public int getItemCount() {
        return boardSize.getNumPairs();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgViewCustom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewCustom = itemView.findViewById(R.id.imgViewCard);
        }

        public void bind(Uri uri) {
            imgViewCustom.setImageURI(uri);
            imgViewCustom.setOnClickListener(null);
        }

        public void bind() {
            imgViewCustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageClickListener.onClickListener();
                }
            });
        }
    }
}
