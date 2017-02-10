package com.otx.nfcreader;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.Snackbar;

import com.otx.nfcreader.card.CardManager;
import com.otx.nfcreader.card.Record;
import com.otx.nfcreader.card.Result;
import com.otx.nfcreader.card.pboc.PbocCard;
import com.otx.nfcreader.card.OctopusCard;

import java.util.ArrayList;

/**
 * Created by wangjun on 2017/2/10.
 */
public class NFCReaderActivity extends AppCompatActivity  {
    private Toolbar toolbar;
    private TextView CardType;
    private TextView Balance;
    private TextView CardId;
    private TextView CardVersion;
    private TextView CardDate;
    private TextView CardCount;
    private RecyclerView mRecyclerView;


    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Resources res;
    private ArrayList<Record> list;
    private RecordAdapter adapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                final int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,
                        NfcAdapter.STATE_OFF);
                switch (state) {
                    case NfcAdapter.STATE_OFF:
                        if(!nfcAdapter.isEnabled()){
                            Snackbar.make(findViewById(android.R.id.content), "NFC已关闭", Snackbar.LENGTH_INDEFINITE).show();
                        }
                        refreshStatus();
                        break;
                    case NfcAdapter.STATE_TURNING_OFF:
                        break;
                    case NfcAdapter.STATE_ON:
                        Snackbar.make(findViewById(android.R.id.content), "NFC已开启", Snackbar.LENGTH_LONG).show();
                        refreshStatus();
                        break;
                    case NfcAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_reader_activity_layout);

        final Resources res = getResources();
        this.res = res;

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        CardType=(TextView)findViewById(R.id.CardType);
        Balance=(TextView)findViewById(R.id.Balance);
        CardId=(TextView)findViewById(R.id.CardId);
        CardVersion=(TextView)findViewById(R.id.CardVersion);
        CardDate=(TextView)findViewById(R.id.CardDate);
        CardCount=(TextView)findViewById(R.id.CardCount);
        mRecyclerView=(RecyclerView)findViewById(R.id.ChargeList);


        toolbar.setTitle(res.getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordAdapter(this, list);
        mRecyclerView.setAdapter(adapter);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        onNewIntent(getIntent());

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    CardManager.FILTERS, CardManager.TECHLISTS);
            if(!nfcAdapter.isEnabled()){
                Snackbar.make(findViewById(android.R.id.content), "请开启NFC", Snackbar.LENGTH_INDEFINITE)
                        .setAction("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.color_blue))
                        .show();
            }
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "您的硬件不支持NFC", Snackbar.LENGTH_INDEFINITE)
                    .setAction("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.color_blue))
                    .show();
        }
        refreshStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the broadcast listener
        this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("NFCTAG", intent.getAction());
        //TODO:
        if(p!=null) {
            Result result=CardManager.load(p, res);
            updateView(result);
        }
    }

    private void updateView(Result result) {
        if(result==null) return;
        if(result.getType()== Result.CardType.OctopusCard){
            OctopusCard p=(OctopusCard) result.getRes();
            CardType.setText(p.getCardName());
            Balance.setText("余额:"+p.getCardCash()+"港币");
            CardId.setText("标识:"+p.getCardId());
            CardVersion.setText("参数:"+p.getCardPm());
            CardDate.setText("有效日期:");
            CardCount.setText("使用次数:");
            adapter.setHK(true);
            list.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        if(result.getType()== Result.CardType.PbocCard){
            PbocCard p=(PbocCard) result.getRes();
            CardType.setText(p.getCardName());
            Balance.setText("余额:"+p.getCardCash()+"元");
            CardId.setText("卡号:"+p.getCardSerl());
            CardVersion.setText("版本:"+p.getCardVersion());
            CardDate.setText("有效日期:"+p.getCardDate());
            CardCount.setText("使用次数:"+p.getCardCount());

            list=p.getRecords();
            adapter=new RecordAdapter(this,list);
            adapter.setHK(false);
            Log.e("LIST",list.size()+"");
            mRecyclerView.setAdapter(adapter);
            return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshStatus();
    }

    private void refreshStatus() {
        final Resources r = this.res;

        final String tip;
        if (nfcAdapter == null)
            tip = r.getString(R.string.tip_nfc_notfound);
        else if (nfcAdapter.isEnabled())
            tip = r.getString(R.string.tip_nfc_enabled);
        else
            tip = r.getString(R.string.tip_nfc_disabled);

        final StringBuilder s = new StringBuilder(
                r.getString(R.string.app_name));

        s.append("  --  ").append(tip);
        toolbar.setTitle(s);
    }
}
