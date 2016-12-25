package com.smokebreaker.www.pl.tips;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.smokebreaker.www.pl.tips.TipItem;
import com.smokebreaker.www.App;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.UsersManager;
import com.smokebreaker.www.bl.models.Tip;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TipsFragment extends RxFragment {

    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Inject
    TipsManager tipsManager;

    @Inject
    UsersManager usersManager;

    FastItemAdapter<TipItem> adapter;

    public TipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        tipsManager.getTips()
                .compose(bindToLifecycle())
                .subscribe(this::setTips);
    }

    private void setTips(List<Tip> tips){
        List<TipItem> items = new ArrayList<>();
        for(int i = 0; i < tips.size() ; i++)
            items.add(new TipItem(this,tips.get(i),tips.get(i).getUid().equals(usersManager.getCurrentUser().getUid())));
        Collections.reverse(items);
        adapter.set(items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tips, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        IconicsDrawable icon = new IconicsDrawable(view.getContext())
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.WHITE);
        fab.setImageDrawable(icon);
        adapter = new FastItemAdapter<>();

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    public void onAddButtonClick(View view){
        new MaterialDialog.Builder(getActivity())
                .title(R.string.add_tip_dialog_title)
                .canceledOnTouchOutside(false)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRangeRes(2, 400, R.color.md_red_900)
                .input(null, null, (dialog, input) -> {
                    tipsManager.addTip(input.toString());
                })
                .negativeText(R.string.cancel)
                .show();
    }

    public void onDelete(Tip tip, int adapterPosition) {
        tipsManager.deleteTip(tip);
    }

    public void onVote(Tip tip,int position) {
        tipsManager.vote(tip,!tip.getVote());
        adapter.notifyItemChanged(position);
    }
}
