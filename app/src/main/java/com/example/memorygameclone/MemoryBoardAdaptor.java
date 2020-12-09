package com.example.memorygameclone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorygameclone.models.BoardSize;
import com.example.memorygameclone.models.MemoryCard;

import java.util.List;

public class MemoryBoardAdaptor extends RecyclerView.Adapter<MemoryBoardAdaptor.ViewHolder> {

    private Context context;
    private BoardSize boardSize;
    private List<MemoryCard> cards;

    private static final String TAG = "MemoryBoardAdaptor";
    private static final int MARGIN = 10;

    interface CardClicked{
        void onClickListener(int position);
    }

    private final CardClicked cardClicked;

    public MemoryBoardAdaptor(Context context, BoardSize boardSize, List<MemoryCard> cards, CardClicked cardClicked) {
        this.context = context;
        this.boardSize = boardSize;
        this.cards = cards;
        this.cardClicked = cardClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int cardWidth = parent.getWidth() / boardSize.getWidth() - (2 * MARGIN);
        int cardHeight = parent.getHeight() / boardSize.getHeight() - (2 * MARGIN);
        int cardLength = Math.min(cardHeight, cardWidth);
        View view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.findViewById(R.id.CardView).getLayoutParams();
        params.height = cardLength;
        params.width = cardLength;
        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemoryCard memoryCard = cards.get(position);

        if (memoryCard.isFaceUp()){
            if (memoryCard.getImageString() == null){
                holder.imageView.setImageResource(memoryCard.getIdentifier());
            }
            else{
                Glide.with(context)
                        .asBitmap()
                        .placeholder(R.drawable.ic_image)
                        .load(memoryCard.getImageString())
                        .into(holder.imageView);
            }
        }else {
            holder.imageView.setImageResource(R.drawable.images);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked.onClickListener(position);
            }
        });

        holder.imageView.setAlpha((memoryCard.isMatched()) ? 0.4f : 1.0f);
        ColorStateList colorStateList = (memoryCard.isMatched()) ? ContextCompat.getColorStateList(context, R.color.color_grey) : null;
        ViewCompat.setBackgroundTintList(holder.imageView, colorStateList);
    }

    @Override
    public int getItemCount() {
        return boardSize.getBoardSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ImageView);

        }

    }
}
