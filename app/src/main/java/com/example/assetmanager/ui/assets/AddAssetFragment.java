package com.example.assetmanager.ui.assets;

import static android.app.Activity.RESULT_OK;
import static com.example.assetmanager.R.*;
import static com.example.assetmanager.util.Constants.REQUEST_PERMISSION_CODE_SCAN;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.assets.tasks.AddAssetTask;
import com.example.assetmanager.ui.assets.tasks.UpdateAssetTask;
import com.example.assetmanager.ui.employees.tasks.FetchEmployeesTask;
import com.example.assetmanager.ui.locations.tasks.FetchLocationsTask;
import com.example.assetmanager.util.Constants;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddAssetFragment extends Fragment {

    private Uri photoUri;
    private EditText editTextAssetName, editTextDescription, editTextPrice, editTextBarcode;
    private Spinner spinnerEmployee, spinnerLocation;
    private ImageView imageViewAsset, imageViewScanBarcode;
    private Uri imageUri;
    private Button btnUploadImage, btnSave;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    protected List<Location> locations = new ArrayList<>();

    protected List<Employee> employees = new ArrayList<>();
    private boolean locationsLoaded = false;
    private boolean employeesLoaded = false;
    private Asset currentAsset;


    @SuppressLint("SuspiciousIndentation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_asset, container, false);

        if (getArguments() != null && getArguments().containsKey("asset")) {
            currentAsset = (Asset) getArguments().getSerializable("asset");
            if (getActivity() != null) {
                Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(getString(R.string.edit_asset));
            }
        }
        new FetchLocationsTask(this).execute();
        new FetchEmployeesTask(this).execute();

        setupUI(view);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        imageViewAsset.setImageURI(photoUri);
                        imageUri = photoUri;
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        imageUri = selectedImageUri;
                        imageViewAsset.setImageURI(selectedImageUri);
                    }
                });

        btnUploadImage.setOnClickListener(v -> showImagePickerOptions());

        imageViewScanBarcode.setOnClickListener(v -> startBarcodeScanner());

        btnSave.setOnClickListener(v -> saveOrUpdateAsset());

        return view;
    }

    private void setupUI(View view) {
        editTextAssetName = view.findViewById(R.id.editTextAssetName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextBarcode = view.findViewById(R.id.editTextBarcode);
        spinnerEmployee = view.findViewById(R.id.spinnerEmployee);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        imageViewAsset = view.findViewById(R.id.imageViewAsset);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageViewScanBarcode = view.findViewById(R.id.ivScanBarcode);
        btnSave = view.findViewById(R.id.btnSave);

        if (currentAsset != null) {

            setupUIWithAssetData();
        }
    }

    private void showImagePickerOptions() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_image))
                .setItems(new String[]{getString(R.string.camera), getString(R.string.gallery)}, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_PERMISSION_CAMERA);
                        } else {
                            try {
                                openCamera();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, Constants.REQUEST_PERMISSION_GALLERY);
                        } else {
                            openGallery();
                        }
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constants.REQUEST_PERMISSION_CAMERA:
                    try {
                        openCamera();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case Constants.REQUEST_PERMISSION_GALLERY:
                    openGallery();
                    break;
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() throws IOException {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(requireContext(), "com.example.assetmanager.fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(cameraIntent);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    public void startBarcodeScanner() {
        Intent intent = new Intent(getContext(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_PERMISSION_CODE_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_PERMISSION_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                if (barcode != null) {
                    editTextBarcode.setText(barcode);
                }
            }
        }
    }

    private void saveOrUpdateAsset() {
        String assetName = editTextAssetName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String barcode = editTextBarcode.getText().toString().trim();

        if (assetName.isEmpty() || description.isEmpty() || price.isEmpty() || barcode.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        Location selectedLocation = (Location) spinnerLocation.getSelectedItem();
        Employee selectedEmployee = (Employee) spinnerEmployee.getSelectedItem();

        String fileName = null;

        if (imageUri != null) {
            Bitmap bitmap = getBitmapFromUri(imageUri);
            if (bitmap != null) {
                fileName = "asset_" + System.currentTimeMillis() + ".jpg";
                saveImageToExternalStorage(bitmap, fileName);
            } else {
                Toast.makeText(getContext(), getString(R.string.failed_to_load_image), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (currentAsset != null) {
            fileName = currentAsset.getImageUrl();
        }

        if (fileName == null) {
            Toast.makeText(getContext(), getString(R.string.no_image_available), Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAsset == null) {
            Asset newAsset = new Asset(Long.parseLong(barcode), assetName, description, Double.parseDouble(price),
                    selectedEmployee.getEmployeeId(), selectedLocation.getLocationId(), fileName);
            Log.d("AssetSave", "New Asset created: " + newAsset);
            new AddAssetTask(AddAssetFragment.this, newAsset).execute();
        } else {

            currentAsset.setName(assetName);
            currentAsset.setDescription(description);
            currentAsset.setPrice(Double.parseDouble(price));
           // currentAsset.setBarcode(Long.parseLong(barcode));
            currentAsset.setEmployeeId(selectedEmployee.getEmployeeId());
            currentAsset.setLocationId(selectedLocation.getLocationId());
            currentAsset.setImageUrl(fileName);
            Log.d("AssetUpdate", "Asset updated: " + currentAsset);
            new UpdateAssetTask(AddAssetFragment.this, currentAsset).execute();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        } catch (IOException e) {
            Log.e("BitmapError", "Error converting Uri to Bitmap: " + e.getMessage());
            return null;
        }
    }

    public void onLocationsFetched(List<Location> locations) {
        if (locations != null) {
            this.locations = locations;
            locationsLoaded = true;

            ArrayAdapter<Location> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    locations
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLocation.setAdapter(adapter);

            checkIfDataReady();
        }
    }

    public void onEmployeesFetched(List<Employee> employees) {
        if (employees != null) {
            this.employees = employees;
            employeesLoaded = true;

            ArrayAdapter<Employee> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    employees
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEmployee.setAdapter(adapter);

            checkIfDataReady();
        }
    }

    private void checkIfDataReady() {
        if (locationsLoaded && employeesLoaded) {
            setupUIWithAssetData();
        }
    }

    private void setupUIWithAssetData() {
        if (currentAsset != null) {
            editTextAssetName.setText(currentAsset.getName());
            editTextDescription.setText(currentAsset.getDescription());
            editTextPrice.setText(String.valueOf(currentAsset.getPrice()));
            editTextBarcode.setText(String.valueOf(currentAsset.getBarcode()));
            editTextBarcode.setEnabled(false);
            imageViewScanBarcode.setVisibility(View.GONE);
            long selectedLocationId = currentAsset.getLocationId();
            int locationPosition = -1;
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i).getLocationId() == selectedLocationId) {
                    locationPosition = i;
                    break;
                }
            }
            if (locationPosition >= 0) {
                spinnerLocation.setSelection(locationPosition);
            }

            long selectedEmployeeId = currentAsset.getEmployeeId();
            int employeePosition = -1;
            for (int i = 0; i < employees.size(); i++) {
                if (employees.get(i).getEmployeeId() == selectedEmployeeId) {
                    employeePosition = i;
                    break;
                }
            }
            if (employeePosition >= 0) {
                spinnerEmployee.setSelection(employeePosition);
            }

            if (currentAsset.getImageUrl() != null) {
                String imagePath = currentAsset.getImageUrl();
                loadImageFromExternalStorage(imagePath);
            }
        }
    }

    private void saveImageToExternalStorage(Bitmap bitmap, String fileName) {
        File externalDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalDir != null) {
            File file = new File(externalDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                Log.d("SaveImage", "Image saved to external storage: " + file.getAbsolutePath());
            } catch (IOException e) {
                Log.e("SaveImage", "Error saving image: " + e.getMessage());
            }
        }
    }

    private void loadImageFromExternalStorage(String fileName) {
        File externalDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(externalDir, fileName);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageViewAsset.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFields() {
        editTextAssetName.setText("");
        editTextDescription.setText("");
        editTextPrice.setText("");
        editTextBarcode.setText("");
        spinnerEmployee.setSelection(0);
        spinnerLocation.setSelection(0);
        imageViewAsset.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.image_placeholder));
        imageUri = null;
    }

}