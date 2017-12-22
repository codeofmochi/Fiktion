package ch.epfl.sweng.fiktion.views.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.views.ProfileActivity;

/**
 * A utility class for displaying User
 * Created by dialexo on 08.12.17.
 */

public class UserDisplayer {

    public static View createUserListElement(final User u, final Context ctx) {
        // create the layout
        LinearLayout v = new LinearLayout(ctx);
        v.setOrientation(LinearLayout.HORIZONTAL);
        v.setPadding(0, 15, 0, 0);

        // profile picture
        final ImageView pic = new ImageView(ctx);
        LinearLayout.LayoutParams picParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        pic.setLayoutParams(picParams);

        Bitmap src = POIDisplayer.scaleBitmap(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.default_user), 120);
        RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(ctx.getResources(), src);
        round.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        pic.setImageDrawable(round);
        v.addView(pic);

        PhotoProvider.getInstance().downloadUserBitmap(u.getID(), PhotoProvider.UserPhotoType.PROFILE, new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onFailure() { /* nothing */ }

            @Override
            public void onNewValue(Bitmap value) {
                Bitmap src = POIDisplayer.cropBitmapToSquare(value);
                src = POIDisplayer.scaleBitmap(src, 120);
                RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(ctx.getResources(), src);
                round.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
                pic.setImageDrawable(round);
            }
        });

        // user infos layout
        LinearLayout texts = new LinearLayout(ctx);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 10);
        texts.setLayoutParams(textsParams);
        texts.setPadding(20, 13, 20, 0);

        // nickname
        TextView nickname = new TextView(ctx);
        nickname.setText(u.getName());
        nickname.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
        nickname.setTextSize(16);
        nickname.setTypeface(null, Typeface.BOLD);
        texts.addView(nickname);

        // name
        TextView nameView = new TextView(ctx);
        String name = u.getPersonalUserInfos().getFirstName() + " " + u.getPersonalUserInfos().getLastName();
        nameView.setText(name);
        nameView.setTextSize(15);
        texts.addView(nameView);

        v.addView(texts);

        // add onClick to visit the user's profile
        View.OnClickListener visitProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, ProfileActivity.class);
                i.putExtra(ProfileActivity.USER_ID_KEY, u.getID());
                ctx.startActivity(i);
            }
        };
        pic.setOnClickListener(visitProfile);
        texts.setOnClickListener(visitProfile);

        return v;
    }

    /**
     * Adds a V button on the right of a linear layout, made for a UserListElement View
     *
     * @param view            the previously created linear layout in which we want to add a V button
     * @param onClickListener what happens on the button press
     * @param ctx             the context where the view is created
     * @return a view with the original layout and a V button added
     */
    public static View withV(LinearLayout view, View.OnClickListener onClickListener, Context ctx) {
        // v button
        ImageButton v = new ImageButton(ctx);
        v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.v_icon_20));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(0, 15, 0, 0);
        v.setLayoutParams(params);
        v.setOnClickListener(onClickListener);
        view.addView(v);

        return view;
    }

    /**
     * Adds a X button on the right of a linear layout, made for a UserListElement View
     *
     * @param view            the previously created linear layout in which we want to add a X button
     * @param onClickListener what happens on the button press
     * @param ctx             the context where the view is created
     * @return a view with the original layout and a X button added
     */
    public static View withX(LinearLayout view, View.OnClickListener onClickListener, Context ctx) {
        // x button
        ImageButton v = new ImageButton(ctx);
        v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.x_icon_20));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(0, 15, 0, 0);
        v.setLayoutParams(params);
        v.setOnClickListener(onClickListener);
        view.addView(v);

        return view;
    }
}
