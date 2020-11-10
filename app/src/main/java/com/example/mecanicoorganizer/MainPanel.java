package com.example.mecanicoorganizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mecanicoorganizer.model.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainPanel extends AppCompatActivity {
    private Toolbar toolbar = null;
    private FloatingActionButton floatingActionButton = null;
    private DatabaseReference databaseReference = null;
    private FirebaseAuth firebaseAuth = null;
    private RecyclerView recyclerView;
    TextView vehiclesAmmountTextView;
    TextView finishedVehiclesAmmountTextView;
    TextView inProgressVehiclesAmmountTextView;
    TextView notStartedVehiclesAmmountTextView;
    ImageView vehiclePhotoImageView;


    private String brand;
    private String model;
    private String regNumber;
    private String year;
    private String color;
    private String customerComments;
    private String diagnosis;
    private double price;
    private String customerPhoneNumber;
    private String status;
    private String position_key;
    private String vehiclePhotoString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);

        toolbar = findViewById(R.id.mainPanelToolbar);
        vehiclesAmmountTextView = findViewById(R.id.vehiclesAmmount);
        finishedVehiclesAmmountTextView = findViewById(R.id.finishedVehiclesAmmount);
        inProgressVehiclesAmmountTextView = findViewById(R.id.inProgressVehiclesAmmount);
        notStartedVehiclesAmmountTextView = findViewById(R.id.notStartedVehiclesAmmount);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Samochody z twojego garażu");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Vehicles").child(userId);
        databaseReference.keepSynced(true);
        recyclerView = findViewById(R.id.MainPanelRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int allVehiclesAmmount = 0;
                int notStartedVehiclesAmmount = 0;
                int inProgressVehiclesAmmount = 0;
                int finishedVehiclesAmmount = 0;

                for(DataSnapshot snap:snapshot.getChildren()) {
                    Vehicle vehicle = snap.getValue(Vehicle.class);
                    allVehiclesAmmount++;

                    if(vehicle.getStatus().contains("Nietknięty")) {
                        notStartedVehiclesAmmount++;
                    }

                    if(vehicle.getStatus().contains("Prace trwają")) {
                        inProgressVehiclesAmmount++;
                    }

                    if(vehicle.getStatus().contains("Zrobiony")) {
                        finishedVehiclesAmmount++;
                    }
                }
                vehiclesAmmountTextView.setText(String.valueOf(allVehiclesAmmount));
                notStartedVehiclesAmmountTextView.setText(String.valueOf(notStartedVehiclesAmmount));
                inProgressVehiclesAmmountTextView.setText(String.valueOf(inProgressVehiclesAmmount));
                finishedVehiclesAmmountTextView.setText(String.valueOf(finishedVehiclesAmmount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, 111);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmapAndSaveToFirebase(imageBitmap);
            vehiclePhotoImageView.setImageBitmap(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        vehiclePhotoString = imageEncoded;
    }

    public Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private void openDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainPanel.this);
        AlertDialog alertDialog = alertDialogBuilder.create();
        LayoutInflater layoutInflater = LayoutInflater.from(MainPanel.this);
        View dialogView = layoutInflater.inflate(R.layout.activity_add_vehicle, null);

        vehiclePhotoImageView = dialogView.findViewById(R.id.vehiclePhotoImageView);
        Button addVehiclePhotoButton = dialogView.findViewById(R.id.addVehiclePhotoButton);
        EditText modelEditText = dialogView.findViewById(R.id.modelEditText);
        EditText brandEditText = dialogView.findViewById(R.id.brandEditText);
        EditText regNumberEditText = dialogView.findViewById(R.id.regNumberEditText);
        EditText yearEditText = dialogView.findViewById(R.id.yearEditText);
        EditText colorEditText = dialogView.findViewById(R.id.colorEditText);
        EditText customerCommentsEditText = dialogView.findViewById(R.id.customerCommentsEditText);
        EditText diagnosisEditText = dialogView.findViewById(R.id.diagnosisEditText);
        EditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        EditText customerPhoneNumber = dialogView.findViewById(R.id.customerPhoneNumberEditText);
        Spinner statusSpinner = dialogView.findViewById(R.id.statusSpinner);
        Button addVehicleButton = dialogView.findViewById(R.id.addVehicleButton);
        String[] statusOptions = new String[]{"Nietknięty","Prace trwają","Zrobiony"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusOptions);
        statusSpinner.setAdapter(statusAdapter);

        addVehiclePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });

        addVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _model = modelEditText.getText().toString().trim();
                String _brand = brandEditText.getText().toString().trim();
                String _regNumber = regNumberEditText.getText().toString().trim();
                String _year = yearEditText.getText().toString().trim();
                String _color = colorEditText.getText().toString().trim();
                String _customerComments = customerCommentsEditText.getText().toString().trim();
                String _diagnosis = diagnosisEditText.getText().toString().trim();
                String _price = priceEditText.getText().toString().trim();
                String _customerPhoneNumber = customerPhoneNumber.getText().toString().trim();
                String _status = statusSpinner.getSelectedItem().toString().trim();
                double _doublePrice = 0;

                if(!TextUtils.isEmpty(_price)) {
                    _doublePrice = Double.parseDouble(_price);
                }

                if(TextUtils.isEmpty(_model)) {
                    modelEditText.setError("Musisz podać model samochodu");
                    return;
                }

                if(TextUtils.isEmpty(_brand)) {
                    brandEditText.setError("Musisz podać markę samochodu");
                    return;
                }

                String id = databaseReference.push().getKey();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String additionDate = simpleDateFormat.format(new Date());
                Vehicle vehicle  = new Vehicle(_model,_brand,_regNumber,_year,_color,_customerComments,
                        _diagnosis,_doublePrice,_status,false,_customerPhoneNumber, vehiclePhotoString,additionDate,id);

                databaseReference.child(id).setValue(vehicle);

                Toast.makeText(getApplicationContext(),
                        "Brawo! Samochód już nie tylko jest w garażu (na podwórku) ale i w twojej bazie!",Toast.LENGTH_LONG).show();

                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Vehicle, MainPanelViewHolder>adapter=new FirebaseRecyclerAdapter<Vehicle, MainPanelViewHolder>(
                Vehicle.class,
                R.layout.vehicle_tile,
                MainPanelViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(MainPanelViewHolder mainPanelViewHolder, Vehicle vehicle, int position) {
                mainPanelViewHolder.setGaragedSinceDate(vehicle.getAdditionDate(), vehicle.getEdited());
                mainPanelViewHolder.setBrand(vehicle.getBrand());
                mainPanelViewHolder.setModel(vehicle.getModel());
                mainPanelViewHolder.setRegNumber(vehicle.getRegNumber());
                mainPanelViewHolder.setStatus(vehicle.getStatus());

                mainPanelViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        position_key = getRef(position).getKey();
                        brand = vehicle.getBrand();
                        model = vehicle.getModel();
                        regNumber = vehicle.getRegNumber();
                        year = vehicle.getYear();
                        color = vehicle.getColor();
                        customerComments = vehicle.getCustomerComments();
                        diagnosis = vehicle.getDiagnosis();
                        price = vehicle.getPrice();
                        customerPhoneNumber = vehicle.getCustomerPhoneNumber();
                        status = vehicle.getStatus();
                        vehiclePhotoString = vehicle.getVehiclePhotoUri();

                        updateVehicle();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MainPanelViewHolder extends RecyclerView.ViewHolder {

        View view = null;

        public MainPanelViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setBrand(String brand) {
            TextView brandTextView = view.findViewById(R.id.brand);
            brandTextView.setText(brand);
        }

        public void setModel(String model) {
            TextView modelTextView = view.findViewById(R.id.model);
            modelTextView.setText(model);
        }

        public void setRegNumber(String regNumber) {
            TextView regNumberTextView = view.findViewById(R.id.regNumber);
            regNumberTextView.setText(regNumber);
        }

        public void setStatus(String status) {
            TextView statusTextView = view.findViewById(R.id.status);
            statusTextView.setText(status);
        }

        public void setGaragedSinceDate(String additionDateString, boolean edited) {
            TextView garagedSinceDateTextView = view.findViewById(R.id.garagedSinceDate);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date additionDate = new Date();
            Date today = new Date();
            String garagedSinceDays;

            try {
                additionDate = format.parse(additionDateString);
             } catch (ParseException e) {
                e.printStackTrace();
            }

            long diff = today.getTime() - additionDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            String addedOrEditedPrefix = edited ? "Edydowany " : "Dodany ";


            garagedSinceDays = days == 0 ?
                    addedOrEditedPrefix.concat("\ndzisiaj") :  days == 1 ?
                    addedOrEditedPrefix.concat("\n ").concat(Long.toString(days)).concat("dzień \ntemu") :
                    addedOrEditedPrefix.concat("\n ").concat(Long.toString(days)).concat("dni \ntemu");



            garagedSinceDateTextView.setText(garagedSinceDays);
        }
    }

    public void updateVehicle() {
        AlertDialog.Builder updateVehicleDialog = new AlertDialog.Builder(MainPanel.this);
        LayoutInflater layoutInflater = LayoutInflater.from(MainPanel.this);
        View updateView = layoutInflater.inflate(R.layout.activity_edit_vehicle, null);

        AlertDialog alertDialog = updateVehicleDialog.create();
        vehiclePhotoImageView = updateView.findViewById(R.id.vehiclePhotoEditImageView);
        EditText modelUpdateEditText = updateView.findViewById(R.id.modelUpdateEditText);
        EditText brandUpdateEditText = updateView.findViewById(R.id.brandUpdateEditText);
        EditText regNumberUpdateEditText = updateView.findViewById(R.id.regNumberUpdateEditText);
        EditText yearUpdateEditText = updateView.findViewById(R.id.yearUpdateEditText);
        EditText colorUpdateEditText = updateView.findViewById(R.id.colorUpdateEditText);
        EditText customerCommentsUpdateEditText = updateView.findViewById(R.id.customerCommentsUpdateEditText);
        EditText diagnosisUpdateEditText = updateView.findViewById(R.id.diagnosisUpdateEditText);
        EditText priceUpdateEditText = updateView.findViewById(R.id.priceUpdateEditText);
        EditText customerPhoneNumberUpdateEditText = updateView.findViewById(R.id.customerPhoneNumberUpdateEditText);
        Spinner statusUpdateSpinner = updateView.findViewById(R.id.statusUpdateSpinner);
        Button updateVehicleButton = updateView.findViewById(R.id.updateVehicleButton);
        Button deleteVehicleButton = updateView.findViewById(R.id.deleteVehicleButton);
        Button editVehiclePhotoButton = updateView.findViewById(R.id.editVehiclePhotoButton);
        String[] statusOptions = new String[]{"Nietknięty","Prace trwają","Zrobiony"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusOptions);
        statusUpdateSpinner.setAdapter(statusAdapter);



        int selectedStatusPosition = Arrays.asList(statusOptions).indexOf(status);
        modelUpdateEditText.setText(model);
        modelUpdateEditText.setSelection(model != null ? model.length(): 0);
        brandUpdateEditText.setText(brand);
        brandUpdateEditText.setSelection(brand != null ? brand.length(): 0);
        regNumberUpdateEditText.setText(regNumber);
        regNumberUpdateEditText.setSelection(regNumber != null ? regNumber.length() : 0);
        yearUpdateEditText.setText(year);
        yearUpdateEditText.setSelection(year != null ?year.length() : 0);
        colorUpdateEditText.setText(color);
        colorUpdateEditText.setSelection(color != null ? color.length(): 0);
        customerCommentsUpdateEditText.setText(customerComments);
        customerCommentsUpdateEditText.setSelection(customerComments != null ? customerComments.length() : 0);
        diagnosisUpdateEditText.setText(diagnosis);
        diagnosisUpdateEditText.setSelection(diagnosis != null ? diagnosis.length() : 0);
        priceUpdateEditText.setText(String.valueOf(price));
        priceUpdateEditText.setSelection(String.valueOf(price).length());
        customerPhoneNumberUpdateEditText.setText(customerPhoneNumber);
        customerPhoneNumberUpdateEditText.setSelection(customerPhoneNumber != null ? customerPhoneNumber.length() : 0);
        statusUpdateSpinner.setSelection(selectedStatusPosition);
        if(vehiclePhotoString != null) {
            vehiclePhotoImageView.setImageBitmap(decodeFromFirebaseBase64(vehiclePhotoString));
        }

        editVehiclePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });

        updateVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _model = modelUpdateEditText.getText().toString().trim();
                String _brand = brandUpdateEditText.getText().toString().trim();
                String _regNumber = regNumberUpdateEditText.getText().toString().trim();
                String _year = yearUpdateEditText.getText().toString().trim();
                String _color = colorUpdateEditText.getText().toString().trim();
                String _customerComments = customerCommentsUpdateEditText.getText().toString().trim();
                String _diagnosis = diagnosisUpdateEditText.getText().toString().trim();
                String _price = priceUpdateEditText.getText().toString().trim();
                String _customerPhoneNumber = customerPhoneNumberUpdateEditText.getText().toString().trim();
                String _status = statusUpdateSpinner.getSelectedItem().toString().trim();
                double _doublePrice = 0;

                if(!TextUtils.isEmpty(_price)) {
                    _doublePrice = Double.parseDouble(_price);
                }

                if(TextUtils.isEmpty(_model)) {
                    modelUpdateEditText.setError("Musisz podać model samochodu");
                    return;
                }

                if(TextUtils.isEmpty(_brand)) {
                    brandUpdateEditText.setError("Musisz podać markę samochodu");
                    return;
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String additionDate = simpleDateFormat.format(new Date());

                Vehicle vehicle  = new Vehicle(_model,_brand,_regNumber,_year,_color,_customerComments,
                        _diagnosis,_doublePrice,_status,true,_customerPhoneNumber,vehiclePhotoString,additionDate,position_key);

                databaseReference.child(position_key).setValue(vehicle);

                Toast.makeText(getApplicationContext(),
                        "Udało się! Aktualizowanie na bieżąco statusów pojazdów pozwoli Ci lepiej organizować pracę.",Toast.LENGTH_LONG).show();

                alertDialog.dismiss();
            }
        });

        deleteVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(position_key).removeValue();
                Toast.makeText(getApplicationContext(),
                        "Pojazd usunięty!Masz racje, im mniejsza lista tym lepiej się nią zarządza",Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(updateView);
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.logOutButton :
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(),
                        "Ciesz sie czasem wolnym, a my przechowamy wszystko czego bedziesz potrzebował",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}