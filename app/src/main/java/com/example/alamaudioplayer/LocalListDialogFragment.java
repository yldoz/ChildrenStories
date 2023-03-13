package com.example.alamaudioplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alamaudioplayer.databinding.FragmentItemListDialogListDialogItemBinding;
import com.example.alamaudioplayer.databinding.FragmentItemListDialogListDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     LocalListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class LocalListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentItemListDialogListDialogBinding binding;
    static Login.OnLangSelect listener;

    // TODO: Customize parameters
    public static LocalListDialogFragment newInstance(ArrayList<Lang> langList, Login.OnLangSelect l) {
        final LocalListDialogFragment fragment = new LocalListDialogFragment();
        final Bundle args = new Bundle();
        listener = l;
        args.putParcelableArrayList(ARG_ITEM_COUNT, langList);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView =view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new LocalAdapter(getArguments().getParcelableArrayList(ARG_ITEM_COUNT)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(FragmentItemListDialogListDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }

    private class LocalAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<Lang> localeList = new ArrayList<>();

        LocalAdapter(ArrayList<Lang> list) {
            this.localeList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(FragmentItemListDialogListDialogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.text.setText(localeList.get(position).locale.getDisplayLanguage().toLowerCase(Locale.GERMAN));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onLangSelectListener(localeList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return localeList.size();
        }

    }
}