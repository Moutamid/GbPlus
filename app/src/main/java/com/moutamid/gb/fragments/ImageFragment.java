package com.moutamid.gb.fragments;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION.SDK_INT;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fxn.stash.Stash;
import com.moutamid.gb.R;
import com.moutamid.gb.adapters.StatusAdapter;
import com.moutamid.gb.databinding.FragmentImageBinding;
import com.moutamid.gb.model.StatusItem;
import com.moutamid.gb.utilis.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageFragment extends Fragment {
    FragmentImageBinding binding;
    List<StatusItem> statusItems = new ArrayList<>();
    StatusAdapter adapter;
    private ProgressBar progressBar;
    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentImageBinding.inflate(getLayoutInflater(), container, false);

        binding.recycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recycler.setHasFixedSize(false);

        progressBar = binding.pbProgress;


        return binding.getRoot();
    }

    private void getItems() {
        statusItems.clear();
        List<StatusItem> allImageItems = new ArrayList<>();
        allImageItems = Constants.allWAImageItems;
        List<StatusItem> finalAllImageItems = allImageItems;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (StatusItem item : finalAllImageItems) {
                    if (new File(Constants.SAVED_FOLDER, item.getFileName()).exists())
                        item.setDownloaded(true);
                    statusItems.add(item);

                    //adapter.notifyItemChanged(statusItems.size() - 1);

                    requireActivity().runOnUiThread(() -> {
                        if (!statusItems.isEmpty()) binding.noStatusLayout.setVisibility(View.GONE);
                        else binding.noStatusLayout.setVisibility(View.VISIBLE);
                        adapter = new StatusAdapter(getContext(), statusItems);
                        binding.recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        }).start();
    }
    private void getStatus() {
        DocumentFile[] allDocumentFiles = getFromSdcard();
        List<StatusItem> tempItems = new ArrayList<>();

        binding.noStatusLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (allDocumentFiles != null) {

                    for (DocumentFile documentFile : allDocumentFiles) {
                        if (!documentFile.getUri().toString().contains(".nomedia")) {
                            if (documentFile.getUri().toString().endsWith(".jpg")
                                    || documentFile.getUri().toString().endsWith(".jpeg")
                                    || documentFile.getUri().toString().endsWith(".png")) {

                                StatusItem item = new StatusItem();
                                item.setFileUri(documentFile.getUri());
                                item.setFileName(documentFile.getName());
                                item.setImage(true);
                                item.setTimeStamp(documentFile.lastModified());
                                Log.e(TAG, "Image Status: " + documentFile.lastModified());
                                //Log.e(TAG, "Get Status Called");

                                if (new File(Constants.SAVED_FOLDER, item.getFileName()).exists())
                                    item.setDownloaded(true);

                                tempItems.add(item);
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusItems.clear();
                        statusItems.addAll(tempItems);
                        if (SDK_INT >= Build.VERSION_CODES.N) {
                            Collections.sort(statusItems, Comparator.comparing(StatusItem::getTimeStamp));
                        }
                        Collections.reverse(statusItems);
                        Constants.allWAImageItems.clear();
                        Constants.allWAImageItems.addAll(statusItems);

                        progressBar.setVisibility(View.GONE);
                        if (!statusItems.isEmpty()) {
                            binding.noStatusLayout.setVisibility(View.GONE);
                        } else {
                            binding.noStatusLayout.setVisibility(View.VISIBLE);
                        }

                        adapter = new StatusAdapter(getContext(), statusItems);
                        binding.recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SDK_INT >= Build.VERSION_CODES.R) {
            getStatus();
        } else {
            getItems();
        }
    }

    private DocumentFile[] getFromSdcard() {
        String str = Stash.getString(Constants.WaSavedRoute);

        DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(str));

        return (documentFile != null && documentFile.exists()) ? documentFile.listFiles() : null;
    }

}