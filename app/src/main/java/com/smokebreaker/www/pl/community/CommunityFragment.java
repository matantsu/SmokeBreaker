package com.smokebreaker.www.pl.community;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.smokebreaker.www.App;
import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.ChatManager;
import com.smokebreaker.www.bl.UsersManager;
import com.smokebreaker.www.bl.models.ChatMessage;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityFragment extends RxFragment{

    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.input)
    EditText input;

    @BindView(R.id.sendButton)
    FloatingActionButton sendButton;

    private FastItemAdapter<ChatItem> adapter;

    @Inject
    ChatManager chatManager;

    @Inject
    UsersManager usersManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void onNewMessage(ChatMessage c) {
        adapter.add(new ChatItem(c,c.getUid().equals(usersManager.getCurrentUser().getUid())));
        rv.smoothScrollToPosition(adapter.getItemCount());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.community,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        IconicsDrawable icon = new IconicsDrawable(view.getContext())
                .icon(GoogleMaterial.Icon.gmd_send)
                .color(Color.WHITE);
        sendButton.setImageDrawable(icon);

        adapter = new FastItemAdapter<>();

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        chatManager.chatStream()
                .compose(bindToLifecycle())
                .subscribe(this::onNewMessage);
    }

    @OnClick(R.id.sendButton)
    public void onSendButtonClick(View v){
        if(!input.getText().toString().isEmpty()){
            chatManager.send(input.getText().toString());
            input.setText("");
        }
    }
}
