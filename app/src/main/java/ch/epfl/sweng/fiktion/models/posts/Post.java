package ch.epfl.sweng.fiktion.models.posts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.epfl.sweng.fiktion.R;

/**
 * A post
 *
 * @author pedro
 */
public abstract class Post {
    private final PostType type;
    private final String id;
    private final Date date;

    /**
     * constructs a post
     *
     * @param type the post type
     * @param id   the id of the post
     * @param date the date of the post
     */
    public Post(PostType type, String id, Date date) {
        this.type = type;
        this.id = id;
        this.date = date;
    }

    /**
     * get the post type
     *
     * @return the type of the post
     */
    public PostType getType() {
        return type;
    }

    /**
     * get the id of the post
     *
     * @return the id of the post
     */
    public String getId() {
        return id;
    }

    /**
     * get the date of the post
     *
     * @return the date of the post
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * equality if parameter is a post and has the same field values
     *
     * @param that the compared object
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof Post && type.equals(((Post) that).getType()) &&
                id.equals(((Post) that).getId()) && date.equals(((Post) that).getDate());
    }

    /**
     * Display a post in the user history
     * To be redefined by each class and call parent displayer
     */
    public abstract View display(Context ctx, String username);

    /**
     * Helper to display a post with a default post layout
     */
    protected View display(View inner, Context ctx) {
        // create new view for this post
        LinearLayout v = new LinearLayout(ctx);
        v.setOrientation(LinearLayout.VERTICAL);

        /* styles */

        // background color
        v.setBackgroundColor(ctx.getResources().getColor(R.color.white));
        // margin
        LinearLayout.LayoutParams vparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vparams.setMargins(2, 10, 2, 10);
        v.setLayoutParams(vparams);
        // padding
        v.setPadding(20, 20, 20, 20);
        // shadow
        v.setElevation(3);

        // add inner top view
        v.addView(inner);

        // date text view
        TextView dateText = new TextView(ctx);
        dateText.setTextColor(ctx.getResources().getColor(R.color.gray));
        dateText.setTextSize(14);
        DateFormat df = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH);
        dateText.setText(df.format(date));
        dateText.setTextColor(ctx.getResources().getColor(R.color.colorText));

        // add date
        v.addView(dateText);

        return v;
    }
}
