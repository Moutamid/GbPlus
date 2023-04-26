package com.moutamid.gb.fragments;

import static android.os.Build.VERSION.SDK_INT;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.moutamid.gb.R;
import com.moutamid.gb.adapters.ShareAdapter;
import com.moutamid.gb.databinding.FragmentImageBinding;
import com.moutamid.gb.databinding.FragmentSavedBinding;
import com.moutamid.gb.model.StatusItem;
import com.moutamid.gb.utilis.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SavedFragment extends Fragment {
    FragmentSavedBinding binding;
    List<StatusItem> statusItems = new ArrayList<>();
    ShareAdapter adapter;
    private ProgressBar progressBar;
    private File savedDirectory;
    private File[] savedFiles;
    public SavedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentSavedBinding.inflate(getLayoutInflater(), container, false);

        binding.recycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recycler.setHasFixedSize(false);

        progressBar = binding.pbProgress;


        return binding.getRoot();
    }

    private void getStatus() {
        savedDirectory = new File(Constants.SAVED_FOLDER);

        if (savedDirectory.exists()) {
            savedFiles = savedDirectory.listFiles();

            if (savedFiles == null) binding.noStatusLayout.setVisibility(View.VISIBLE);
            else getItems(savedFiles);

        } else binding.noStatusLayout.setVisibility(View.VISIBLE);
    }

    private void getItems(File[] savedFiles) {
        statusItems.clear();
        Constants.allSavedItems.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (File sourceFile : savedFiles) {
                    StatusItem item = new StatusItem();
                    if (sourceFile.getPath().endsWith(".jpg") || sourceFile.getPath().endsWith(".jpeg") || sourceFile.getPath().endsWith(".png")) {
                        item = new StatusItem(sourceFile, sourceFile.getName(), sourceFile.getAbsolutePath(), Uri.fromFile(sourceFile), sourceFile.lastModified(), true);
                    } else if (sourceFile.getPath().endsWith(".mp4") || sourceFile.getPath().endsWith(".3gp")) {
                        item = new StatusItem(sourceFile, sourceFile.getName(), sourceFile.getAbsolutePath(), Uri.fromFile(sourceFile), sourceFile.lastModified(), false);
                    }
                    statusItems.add(item);
                    Constants.allSavedItems.add(item);
                }

                if (SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(statusItems, Comparator.comparing(StatusItem::getTimeStamp));
                }
                Collections.reverse(statusItems);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ShareAdapter(requireContext(), statusItems);
                        binding.recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        binding.noStatusLayout.setVisibility(View.GONE);
                    }
                });

            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.isPermissionGranted(requireContext())){
            getStatus();
        }
    }
}