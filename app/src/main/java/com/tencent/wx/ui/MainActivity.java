package com.tencent.wx.ui;

import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.wx.framework.log.L;
import com.tencent.wx.framework.ui.BaseActivity;
import com.tencent.wx.framework.ui.IScan;
import com.tencent.wx.framework.ui.OnDecodeListener;
import com.tencent.wx.scan.ScannerFactory;

import test.decode.com.decodesdk.R;

import static android.media.AudioManager.STREAM_SYSTEM;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SoundPool soundPool;
    private TextView decodeResult;
    private IScan scan;
    private int id;
    private Button button;

    private Button btnOpen;
    private Button btnTriger;
    private Button btnColse;


    private OnDecodeListener decodeListener = new OnDecodeListener() {
        @Override
        public void onDecode(String code) {
            L.e(TAG, "onDecode:" + code);
            decodeResult.setText(code);
            soundPool.play(id, 1, 1, 0, 0, 1);
//            scan.onStopScan();
//            button.setText("开始扫码");
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scan = ScannerFactory.create(decodeListener, false);
        scan.onActivityCreated(this, savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, scan.getContainer());
        decodeResult = findViewById(R.id.decode_result);
        soundPool = new SoundPool(3, STREAM_SYSTEM, 1);
        //load 耗时，正常情况下应该写在子线程
        id = soundPool.load(this, R.raw.beep, 1);

        button = findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("停止扫码".equals(button.getText().toString())) {
                    scan.onStopScan();
                    button.setText("开始扫码");
                } else if ("开始扫码".equals(button.getText().toString())) {
                    scan.onStartScan();
                    button.setText("停止扫码");
                }
            }
        });


        btnOpen = (Button) findViewById(R.id.open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.onStartScan();
            }
        });

        btnTriger = (Button) findViewById(R.id.triger);
        btnTriger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.onTrigerScan();
            }
        });

        btnColse = (Button) findViewById(R.id.colse);
        btnColse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.onStopScan();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        scan.onStartScan();
//        button.setText("停止扫码");
    }


    @Override
    protected void onPause() {
        super.onPause();
        scan.onActivityPaused(this);
//        button.setText("开始扫码");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scan.onActivityDestroyed(this);
    }
}
