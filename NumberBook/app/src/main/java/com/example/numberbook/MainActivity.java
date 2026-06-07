package com.example.numberbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numberbook.adapter.ContactAdapter;
import com.example.numberbook.model.ApiResponse;
import com.example.numberbook.model.Contact;
import com.example.numberbook.network.ContactApi;
import com.example.numberbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACTS_PERMISSION = 100;

    private Button btnLoadContacts;
    private Button btnSyncContacts;
    private Button btnSearch;
    private EditText etKeyword;
    private TextView tvInfo;
    private RecyclerView recyclerViewContacts;

    private ContactAdapter contactAdapter;
    private final List<Contact> localContacts = new ArrayList<>();

    private ContactApi contactApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initRecyclerView();
        initRetrofit();
        initActions();
    }

    private void initViews() {
        btnLoadContacts = findViewById(R.id.btnLoadContacts);
        btnSyncContacts = findViewById(R.id.btnSyncContacts);
        btnSearch = findViewById(R.id.btnSearch);
        etKeyword = findViewById(R.id.etKeyword);
        tvInfo = findViewById(R.id.tvInfo);
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
    }

    private void initRecyclerView() {
        contactAdapter = new ContactAdapter();

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setAdapter(contactAdapter);
    }

    private void initRetrofit() {
        contactApi = RetrofitClient
                .getClient()
                .create(ContactApi.class);
    }

    private void initActions() {
        btnLoadContacts.setOnClickListener(v -> checkPermissionAndLoadContacts());

        btnSyncContacts.setOnClickListener(v -> syncContactsToServer());

        btnSearch.setOnClickListener(v -> searchContactsFromServer());
    }

    private void checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {

            loadContactsFromPhone();

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS_PERMISSION
            );
        }
    }

    private void loadContactsFromPhone() {
        localContacts.clear();

        HashSet<String> uniqueContacts = new HashSet<>();

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            );

            int phoneIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            );

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);

                if (name == null) {
                    name = "Sans nom";
                }

                if (phone == null || phone.trim().isEmpty()) {
                    continue;
                }

                phone = phone.replace(" ", "").trim();

                String key = name + "_" + phone;

                if (!uniqueContacts.contains(key)) {
                    uniqueContacts.add(key);

                    Contact contact = new Contact(name, phone, "android");
                    localContacts.add(contact);
                }
            }

            cursor.close();
        }

        contactAdapter.setContacts(localContacts);

        tvInfo.setText("Contacts lus depuis le téléphone : " + localContacts.size());

        Toast.makeText(
                this,
                localContacts.size() + " contact(s) chargé(s)",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void syncContactsToServer() {
        if (localContacts.isEmpty()) {
            Toast.makeText(
                    this,
                    "Charge d'abord les contacts du téléphone",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, List<Contact>> body = new HashMap<>();
        body.put("contacts", localContacts);

        contactApi.insertContacts(body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call,
                                   @NonNull Response<ApiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    String message = apiResponse.getMessage();

                    if (message == null) {
                        message = "Synchronisation terminée";
                    }

                    tvInfo.setText(
                            message
                                    + " | Ajoutés : " + apiResponse.getInserted()
                                    + " | Échecs : " + apiResponse.getFailed()
                    );

                    Toast.makeText(
                            MainActivity.this,
                            message,
                            Toast.LENGTH_SHORT
                    ).show();

                } else {
                    String errorMessage = "Code serveur : " + response.code();

                    try {
                        if (response.errorBody() != null) {
                            errorMessage += "\n" + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMessage += "\nImpossible de lire l'erreur";
                    }

                    Toast.makeText(
                            MainActivity.this,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();

                    tvInfo.setText(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call,
                                  @NonNull Throwable t) {

                Toast.makeText(
                        MainActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void searchContactsFromServer() {
        String keyword = etKeyword.getText().toString().trim();

        if (keyword.isEmpty()) {
            etKeyword.setError("Saisis un nom ou un numéro");
            return;
        }

        contactApi.searchContacts(keyword).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call,
                                   @NonNull Response<ApiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    List<Contact> results = apiResponse.getContacts();

                    if (results == null) {
                        results = new ArrayList<>();
                    }

                    contactAdapter.setContacts(results);

                    tvInfo.setText("Résultats trouvés : " + results.size());

                    Toast.makeText(
                            MainActivity.this,
                            results.size() + " résultat(s)",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Erreur serveur pendant la recherche",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call,
                                  @NonNull Throwable t) {

                Toast.makeText(
                        MainActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void loadAllContactsFromServer() {
        contactApi.getAllContacts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call,
                                   @NonNull Response<ApiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<Contact> contacts = response.body().getContacts();

                    if (contacts == null) {
                        contacts = new ArrayList<>();
                    }

                    contactAdapter.setContacts(contacts);
                    tvInfo.setText("Contacts enregistrés sur le serveur : " + contacts.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call,
                                  @NonNull Throwable t) {

                Toast.makeText(
                        MainActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                loadContactsFromPhone();

            } else {
                Toast.makeText(
                        this,
                        "Permission refusée : impossible de lire les contacts",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}