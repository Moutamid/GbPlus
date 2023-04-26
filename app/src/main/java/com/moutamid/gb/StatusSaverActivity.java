package com.moutamid.gb;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.moutamid.gb.databinding.ActivityStatusSaverBinding;
import com.moutamid.gb.fragments.ImageFragment;
import com.moutamid.gb.fragments.SavedFragment;
import com.moutamid.gb.fragments.VideoFragment;
import com.moutamid.gb.model.StatusItem;
import com.moutamid.gb.utilis.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StatusSaverActivity extends AppCompatActivity {
    ActivityStatusSaverBinding binding;
    private Uri treeUriWA, uriWA;

    private static final String EXTERNAL_STORAGE_AUTHORITY_PROVIDER = "com.android.externalstorage.documents";
    private static final String ANDROID_DOC_ID_WA = "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusSaverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);
        binding.goback.setOnClickListener(v -> {
            onBackPressed();
        });

        if (new File(Constants.SOURCE_FOLDER_WA_NEW).exists()) {
            uriWA = DocumentsContract.buildDocumentUri(
                    EXTERNAL_STORAGE_AUTHORITY_PROVIDER,
                    ANDROID_DOC_ID_WA
            );

            treeUriWA = DocumentsContract.buildTreeDocumentUri(
                    EXTERNAL_STORAGE_AUTHORITY_PROVIDER,
                    ANDROID_DOC_ID_WA
            );
        }

        if (!Constants.checkIfGotAccess(this, treeUriWA)) {
            if (new File(Constants.SOURCE_FOLDER_WA_NEW).exists() || new File(Constants.SOURCE_FOLDER_WA_OLD).exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    openDirectory();
                }
            }
        } else {
            setupViewPager(binding.viewpager);
            binding.tabs.setupWithViewPager(binding.viewpager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager());

        adapter.addFragment(new ImageFragment(), "Images");
        adapter.addFragment(new VideoFragment(), "Videos");
        adapter.addFragment(new SavedFragment(), "Saved");

        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return this.mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.mFragmentTitleList.get(position);
        }
    }

    private void initFolders() {
        File sourceDirectoryOld = new File(Constants.SOURCE_FOLDER_WA_OLD);
        getItems(sourceDirectoryOld);
    }

    private void getItems(File sourceFolder) {
        if (sourceFolder.exists()) {
            File[] sourceFiles = sourceFolder.listFiles();
            if (sourceFiles != null) getWAFiles(sourceFiles);
        }
    }

    private void getWAFiles(Object[] objects) {
        Constants.allWAImageItems.clear();
        Constants.allWAVideoItems.clear();

        // Arrays.sort(objects, (o1, o2) -> Long.compare(((File) o2).lastModified(), ((File) o1).lastModified()));

        for (File file : (File[]) objects) {
            if (!file.getPath().contains(".nomedia")) {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png")) {
                    Constants.allWAImageItems.add(new StatusItem(file, file.getName(), file.getAbsolutePath(), Uri.fromFile(file), file.lastModified(), true));
                } else if (file.getPath().endsWith(".mp4") || file.getPath().endsWith(".3gp")) {
                    Constants.allWAVideoItems.add(new StatusItem(file, file.getName(), file.getAbsolutePath(), Uri.fromFile(file), file.lastModified(), false));
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openDirectory() {
        Intent intent = getPrimaryVolume().createOpenDocumentTreeIntent();
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriWA);
        handleIntentActivityResult.launch(intent);
    }

    ActivityResultLauncher<Intent> handleIntentActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri directoryUri = result.getData().getData();
                        if (!directoryUri.toString().contains(".Statuses")) {
                            Log.d("myMsg", directoryUri.toString());
                            Toast.makeText(this, "You didn't grant permission to the correct folder", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        askpermision();

                        Stash.put(Constants.WaSavedRoute, directoryUri.toString());
                        setupViewPager(binding.viewpager);
                        binding.tabs.setupWithViewPager(binding.viewpager);
                        initFolders();
                        getContentResolver().takePersistableUriPermission(directoryUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    }

                } else
                    Toast.makeText(this, "You didn't grant any permission", Toast.LENGTH_SHORT).show();
            });

    private void askpermision() {
        if (!Constants.isPermissionGranted(StatusSaverActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_AUDIO);
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_VIDEO);
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES);
                ActivityCompat.requestPermissions(StatusSaverActivity.this, Constants.permissions13, 1);
            } else {
                shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(StatusSaverActivity.this, Constants.permissions, 1);
            }
        }
    }

    private StorageVolume getPrimaryVolume() {
        StorageManager sm = (StorageManager) getSystemService(STORAGE_SERVICE);
        return sm.getPrimaryStorageVolume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.isPermissionGranted(StatusSaverActivity.this)){
            initFolders();
        }
    }
}