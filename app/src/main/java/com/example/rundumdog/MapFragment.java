package com.example.rundumdog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapViewRepository;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MapFragment extends Fragment {

    private MapView map = null;
    private SharedPreferences mPrefs;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private CopyrightOverlay mCopyrightOverlay;
    private MapViewRepository mRepository;
    private EntryListModel viewModel;


    private static final String PREFS_NAME = "org.andnav.osm.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";

    //creates map view with a defined geo point
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(47.8583, 8.8988);
        mapController.setCenter(startPoint);
        viewModel = new ViewModelProvider(requireActivity()).get(EntryListModel.class);

        return view;
    }

    //resumes activity
    public void onResume() {
        super.onResume();
        if (map != null)
            map.onResume();
    }
    //pauses activity
    public void onPause() {

        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, map.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, map.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(map.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(map.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) map.getZoomLevelDouble());
        edit.commit();

        map.onPause();
        super.onPause();
        if (map != null)
            map.onPause();
    }

    // creates map with overlays
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        //My Location
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);


        //Mini map
        mMinimapOverlay = new MinimapOverlay(context, map.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        map.getOverlays().add(this.mMinimapOverlay);


        mCopyrightOverlay = new CopyrightOverlay(context);
        map.getOverlays().add(this.mCopyrightOverlay);


        //compass
        mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);

        //scale bar
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(this.mScaleBarOverlay);


        //rotation
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(this.mRotationGestureOverlay);

        //touch function
        map.setMultiTouchControls(true);

        map.setTilesScaledToDpi(true);


        final float zoomLevel = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1);
        map.getController().setZoom(zoomLevel);
        final float orientation = mPrefs.getFloat(PREFS_ORIENTATION, 0);
        map.setMapOrientation(orientation, false);
        final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);
        map.setExpectedCenter(new GeoPoint(latitude, longitude));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://reise-mit-hund-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        Drawable newMarker = this.getResources().getDrawable(R.drawable.location);
        Drawable newMarkerFocus = this.getResources().getDrawable(R.drawable.location_focus);

        //listens to the database and populates the map with Entry Markers via an overlay
        mDatabase.child("entries").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                }
                else {

                    ArrayList<EntryMarker> list = new ArrayList<EntryMarker>();
                    for(DataSnapshot ds :  task.getResult().getChildren()) {
                        Entry entry = ds.getValue(Entry.class);
                        Log.d("CREATION", entry.toString());
                        DateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
                        String formDate = dtf.format(entry.date);
                        EntryMarker marker = new EntryMarker(entry.title, formDate, new GeoPoint(entry.latitude, entry.longitude), entry.latitude, entry.longitude);
                        list.add(marker);
                    }

                        //overlay
                        ItemizedOverlayWithFocus<EntryMarker> mOverlay = new ItemizedOverlayWithFocus<EntryMarker>(list, newMarker, newMarkerFocus, Color.rgb(202, 138, 91),
                                new ItemizedOverlayWithFocus.OnItemGestureListener<EntryMarker>() {
                                    @Override
                                    public boolean onItemSingleTapUp(final int index, final EntryMarker item) {
                                        //do something
                                        return true;
                                    }

                                    @Override
                                    public boolean onItemLongPress(final int index, final EntryMarker item) {
                                        return false;
                                    }


                                }, context);
                        mOverlay.setFocusItemsOnTap(true);


                        map.getOverlays().add(mOverlay);
                    }


            }
        });


        //marker placement overlay
        MapEventsReceiver mReceive = new MapEventsReceiver() {


            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                return false;
            }

            //creates a dialog on a long press, allowing user to add a new entry in chosen location
            @Override
            public boolean longPressHelper(GeoPoint p) {
                Toast.makeText(getContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Create entry");
                builder.setMessage("Would you like to create a new entry at this location?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            //redirects user to Entry creation Fragment upon agreement and passes the coordinates
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.setPoint(p.getLatitude(), p.getLongitude());
                                replaceFragment(new EntryFragment());
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        };


        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        setHasOptionsMenu(true);
    }

    public MapViewRepository getRepository() {
        return mRepository;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
