package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

public class POIPageActivity extends MenuDrawerActivity implements OnMapReadyCallback {

    public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
        private String[] data;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;
            public ViewHolder(TextView v) {
                super(v);
                text = v;
            }
        }

        public ReviewsAdapter(String[] data) {
            this.data = data;
        }

        @Override
        public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(data[position]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

    private MapView map;
    private RecyclerView reviewsView;
    private RecyclerView.Adapter reviewsAdapter;
    private RecyclerView.LayoutManager reviewsLayout;
    private String[] reviewsData = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec congue dolor at auctor scelerisque. Duis sodales eros velit, sit amet tincidunt ex pharetra ac. Pellentesque pellentesque et augue ut pellentesque. Suspendisse in lacinia nunc. Integer consequat sollicitudin ligula sed finibus.",
            "Curabitur condimentum ligula eu diam maximus porttitor. Interdum et malesuada fames ac ante ipsum primis in faucibus. Suspendisse metus urna, tincidunt sed augue ac, consectetur congue felis. Pellentesque efficitur enim et ultrices pellentesque.",
            "Curabitur quis lectus eu ex volutpat eleifend. Sed iaculis orci ut odio sodales, id lobortis est volutpat. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae",
            "Proin suscipit, mauris quis ullamcorper fringilla, mi nibh cursus felis, aliquet aliquam est ligula ut lacus. Suspendisse in lacus vitae urna ornare posuere ut nec massa. Curabitur maximus ullamcorper venenatis. Nulla pulvinar arcu a purus pulvinar rhoncus. ",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // give layout to parent menu class
        includeLayout = R.layout.activity_poipage;
        super.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        map = (MapView) findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);

        // get POI name
        Intent from = getIntent();
        String poiName = from.getStringExtra("POI_NAME");

        // get POI from database
        Providers.database.getPoi(poiName, new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {

            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });

        // get recycler view for reviews
        reviewsView = (RecyclerView) findViewById(R.id.reviews);
        reviewsLayout = new LinearLayoutManager(this);
        reviewsView.setLayoutManager(reviewsLayout);
        reviewsAdapter = new ReviewsAdapter(reviewsData);
        reviewsView.setAdapter(reviewsAdapter);

        // change text color
        TextView featured = (TextView) findViewById(R.id.featured);
        Spannable span = new SpannableString(featured.getText());
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 12, featured.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        featured.setText(span);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Akihabara marker
        LatLng mark = new LatLng(35.7022077, 139.7722703);
        googleMap.addMarker(new MarkerOptions().position(mark)
                .title("Akihabara"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        map.onResume();
    }

}
