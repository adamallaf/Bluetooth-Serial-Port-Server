package allaf.bluetoothserialportserver;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.icu.util.TimeUnit;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BTServer btServer;
    private TextView tv;
    private Timer receiveTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btServer = null;
        tv = findViewById(R.id.tv_long);
        tv.setMovementMethod(new ScrollingMovementMethod());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        receiveTimer = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
        }
        btServer = new BTServer();
        btServer.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startReceiveTimer();
            }
        }, 1000);
    }

    private void startReceiveTimer() {
        receiveTimer = new Timer();
        receiveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message;
                            message = btServer.messageQueue.poll();
                            if(message != null)
                                updateTextView(message);
                        }
                    });
            }
        }, (0), (200));
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiveTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startReceiveTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(btServer != null)
            btServer.stopServer();
    }

    void updateTextView(String text) {
        tv.append(text);
    }
}
