package com.example.assetmanager.ui.assets;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.assetmanager.R;
import com.example.assetmanager.adapter.AssetsAdapter;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.ui.assets.tasks.DeleteAssetTask;
import com.example.assetmanager.ui.assets.tasks.FetchAssetsTask;
import com.example.assetmanager.ui.assets.tasks.SearchAssetTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AssetsFragment extends Fragment implements AssetsAdapter.OnAssetItemClick,  AssetsAdapter.OnAssetActionListener {
    protected AssetsAdapter adapter;
    private final List<Asset> assets = new ArrayList<>();
    protected List<Asset> allAssets = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assets, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AssetsAdapter(assets, getContext(), this::onAssetClick, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddAsset = view.findViewById(R.id.fabAddAsset);
        fabAddAsset.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_assetsFragment_to_addAssetFragment));

        new FetchAssetsTask(this).execute();

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.search_by_name_or_barcode));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    updateAssetList(allAssets);
                }else {
                    new SearchAssetTask(AssetsFragment.this, assets).execute(newText);
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onAssetClick(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("asset", asset);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_currentFragment_to_assetDetailFragment, bundle);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Asset> assetList){
        assets.clear();
        allAssets.clear();
        assets.addAll(assetList);
        allAssets.addAll(assetList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAssetUpdate(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("asset", asset);
        AddAssetFragment fragment = new AddAssetFragment();
        fragment.setArguments(bundle);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_assetsFragment_to_addAssetFragment, bundle);

    }

    @Override
    public void onAssetDelete(Asset asset) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirm_deletion))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_asset))
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> new DeleteAssetTask(this, asset).execute())
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshAssetList() {
        new FetchAssetsTask(this).execute();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAssetList(List<Asset> filteredAssets) {
        assets.clear();
        assets.addAll(filteredAssets);
        adapter.notifyDataSetChanged();
    }
}