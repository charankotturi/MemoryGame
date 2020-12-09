package com.example.memorygameclone.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.memorygameclone.R;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.R)
public class Constants {
    public final List<Integer> pics = List.of(
            R.drawable.ic_cart,
            R.drawable.ic_face,
            R.drawable.ic_flower,
            R.drawable.ic_headset,
            R.drawable.ic_home,
            R.drawable.ic_light,
            R.drawable.ic_lock,
            R.drawable.ic_moon,
            R.drawable.ic_plane,
            R.drawable.ic_star,
            R.drawable.ic_world,
            R.drawable.ic_tv
    );
}
