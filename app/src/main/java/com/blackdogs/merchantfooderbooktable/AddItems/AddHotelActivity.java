package com.blackdogs.merchantfooderbooktable.AddItems;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackdogs.merchantfooderbooktable.MainActivity;
import com.blackdogs.merchantfooderbooktable.MyApplication;
import com.blackdogs.merchantfooderbooktable.Pojo.HotelList;
import com.blackdogs.merchantfooderbooktable.Pojo.HotelOwner;
import com.blackdogs.merchantfooderbooktable.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

/**
 * Created by BlackDogs on 18-09-2016.
 */
public class AddHotelActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    @BindView(R.id.iv_HotelImage)
    ImageView hotelImage;
    @BindView(R.id.btn_AddHotelImage)
    Button btnHotelImage;
    @BindView(R.id.et_HotelName)
    EditText etHotelName;
    @BindView(R.id.et_HotelLocation)
    EditText etHotelLocation;
    @BindView(R.id.btn_Submit)
    Button submit;

    private static final int SELECT_PICTURE = 100;
    int PLACE_PICKER_REQUEST = 1;
    private Uri imageUri;
    private GoogleApiClient mGoogleApiClient;
    double lat, lng, rating = 0;
    Uri selectedImageUri;
    DatabaseReference mainRef;
    String cityName, hotelName;
    SpotsDialog dialog;
    boolean checkImage = false, checkEditText = false;
    FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addhotel);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();

        mainRef = FirebaseDatabase.getInstance().getReference();

        makeStatusBarTransparent();


        submit.setVisibility(View.INVISIBLE);

        dialog = new SpotsDialog(this, R.style.ProgreeBarCustom);

//        etHotelName.setVisibility(View.INVISIBLE);
        //      etHotelLocation.setVisibility(View.INVISIBLE);

        Glide.with(this).load(R.drawable.hotel_placeholder).placeholder(R.drawable.hotel_placeholder).into(hotelImage);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

    }

    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    @OnClick(R.id.btn_AddHotelImage)
    public void addHotelImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                checkImage = true;
                if (checkImage && checkEditText) {
                    submit.setVisibility(View.VISIBLE);
                }
                // Get the url from data
                selectedImageUri = data.getData();
                setImageUri(selectedImageUri);

                if (null != selectedImageUri) {

                    // Set the image in ImageView
                    Glide.with(this).load(selectedImageUri).centerCrop().into(hotelImage);
                    btnHotelImage.setText("Edit");
                }
            }
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                checkEditText = true;

                if (checkEditText && checkImage) {
                    submit.setVisibility(View.VISIBLE);
                }

                Place place = PlacePicker.getPlace(data, this);

                LatLng ll = place.getLatLng();
                lat = ll.latitude;
                lng = ll.longitude;

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cityName = addresses.get(0).getLocality();
                hotelName = (String) place.getName();

                etHotelName.setVisibility(View.VISIBLE);
                etHotelName.setText(hotelName);


                etHotelLocation.setVisibility(View.VISIBLE);
                etHotelLocation.setText(place.getAddress());
            }
        }

    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @OnClick(R.id.btn_ChooseHotel)
    public void chooseHotelFromMap(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddHotelActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                }, 2909);
            } else {
                pickPlace();
            }
        }


    }

    private void pickPlace() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    pickPlace();
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @OnClick(R.id.btn_Submit)
    public void SubmitHotelDetails(View view) {

        dialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://fooder-bad83.appspot.com");
        StorageReference imagesRef = storageRef.child("hotel_images");
        StorageReference selectimage_ref = imagesRef.child(selectedImageUri.getLastPathSegment());

        // upload file to firebase storage
        selectimage_ref.putFile(selectedImageUri).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri download_url = taskSnapshot.getDownloadUrl();
                        String string_download_url = download_url.toString();

                        String key = mainRef.child("hotelList").push().getKey();
                        String ownerId = auth.getCurrentUser().getUid();
                        HotelList hotelInfo = new HotelList(hotelName, string_download_url, cityName, rating, lat, lng);
                        HotelOwner hotelOwner = new HotelOwner(string_download_url, cityName, hotelName, rating);
                        Map<String, Object> hotelInfoValues = hotelInfo.toMap();
                        Map<String, Object> hotelOwnerValues = hotelOwner.toMap();

                        Map<String, Object> map = new HashMap<>();
                        map.put("/hotelList/" + key, hotelInfoValues);
                        map.put("/hotelOwner/" + ownerId + "/" + key, hotelOwnerValues);
                        mainRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.e("TAG", "Failed to write message", databaseError.toException());
                                    Toast.makeText(AddHotelActivity.this, "Error adding HotelInfo", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {

                                    Toast.makeText(AddHotelActivity.this, "Hotel added successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    startActivity(new Intent(AddHotelActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

                       /* hotelOwnerRef.child(auth.getCurrentUser().getUid()).push().setValue(hotelOwner);

                        hotelListRef.push().setValue(hotelInfo, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.e("TAG", "Failed to write message", databaseError.toException());
                                    Toast.makeText(AddHotelActivity.this, "Error adding HotelInfo", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {

                                    Toast.makeText(AddHotelActivity.this, "Hotel added successfully", Toast.LENGTH_SHORT).show();
                                   dialog.dismiss();
                                    startActivity(new Intent(AddHotelActivity.this,MainActivity.class));
                                    finish();
                                }
                            }
                        });*/

                    }
                });


    }


}

