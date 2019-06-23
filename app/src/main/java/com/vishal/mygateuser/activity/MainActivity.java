package com.vishal.mygateuser.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vishal.mygateuser.R;
import com.vishal.mygateuser.adapters.RvUserAdapter;
import com.vishal.mygateuser.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    private RecyclerView rvUser;
    private FloatingActionButton fabOpenCamera;
    private TextView tvNoDataFound;
    private Uri mImageCaptureUri;
    private Uri fileUri;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<User> usersData = new ArrayList<>();
    private RvUserAdapter rvUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabOpenCamera = findViewById(R.id.fab);
        tvNoDataFound = findViewById(R.id.tv_no_data);
        rvUser = findViewById(R.id.rv_user);
        rvUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvUserAdapter = new RvUserAdapter(usersData, this);
        rvUser.setAdapter(rvUserAdapter);
        sharedPreferences = getSharedPreferences("MYGATE", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setListeners();
        getData();
    }

    private void setListeners() {
        fabOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnableRuntimePermission();
            }
        });
    }

    private void captureImageInitialization() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 7);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("imgUrl", mImageCaptureUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageCaptureUri = savedInstanceState.getParcelable("imgUrl");
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(MainActivity.this, photo);
            File finalFile = new File(getRealPathFromURI(tempUri));
            saveUserInDataBase(String.valueOf(finalFile));
            System.out.println(finalFile);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }

    public void saveUserInDataBase(String img) {
        User user = new User();
        user.setUserName("user" + System.currentTimeMillis());
        user.setPassCode(String.valueOf(getRandomNumber(100000, 900000)));
        user.setImgUrl(String.valueOf(img));
        editor.putString("user" + System.currentTimeMillis(), new Gson().toJson(user));
        editor.commit();
        getData();

    }

    private void getData() {
        usersData.clear();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            User user = new Gson().fromJson(entry.getValue().toString(), User.class);
            usersData.add(user);
            rvUserAdapter.notifyDataSetChanged();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(MainActivity.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImageInitialization();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
