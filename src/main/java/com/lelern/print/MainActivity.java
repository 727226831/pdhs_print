package com.lelern.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.lelern.print.databinding.ActivityMainBinding;
import com.qr285.sdk.OnPrinterListener;
import com.qr285.sdk.PrinterPort;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter = null;
    private PrinterPort printerPort;
  //  private  int i=0;
    ActivityMainBinding binding;
    DataBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.bPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean==null){
                    return;
                }

                if(printerPort==null) {
                    initbluetooth();
                }
                printeData();

            }
        });
        binding.ivPrintpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this)
                        .setCaptureActivity(ScanActivity.class)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        .setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(false)// 是否开启声音,扫完码之后会"哔"的一声
                        .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                        .initiateScan();// 初始化扫码
            }
        });
        binding.etPrintpos.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                 getData();
                }
                return false;
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //扫码
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                String code=result.getContents();
                Log.i("code",code);
                binding.etPrintpos.setText(code);
                getData();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

        private void getData() {

        JSONObject jsonObject=new JSONObject();
        try {

            jsonObject.put("methodname","getPrintList");
            jsonObject.put("acccode","036");
            jsonObject.put("printpos",binding.etPrintpos.getText().toString());



        } catch (JSONException e) {
            e.printStackTrace();
        }
        String obj=jsonObject.toString();
        Log.i("json object",obj);

        Call<ResponseBody> data =Request.getRequestbody(obj);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if(response.code()==200) {

                        JSONArray jsonArray=new JSONArray(response.body().string());
                        Log.i("json",jsonArray.getJSONObject(0).toString());
                       bean=new Gson().fromJson(jsonArray.getJSONObject(0).toString(),DataBean.class);
                       binding.tvCCode.setText("派工单号："+bean.getCCode());
                       binding.tvInvname.setText("描述："+bean.getInvname());
                       binding.tvMocode.setText("生产单号："+bean.getMocode());
                       binding.tvInvstd.setText("零件号："+bean.getInvstd());
                       binding.tvSncount.setText("描述："+bean.getSncount()+"");


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            } });
    }

    private void initbluetooth() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //If the Bluetooth adapter is not supported,programmer is over
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
        printerPort = new PrinterPort(MainActivity.this, new OnPrinterListener() {
            @Override
            public void onConnected() {
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectedFail() {

            }

            @Override
            public void ondisConnected() {
                Toast.makeText(MainActivity.this, "连接断开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ondisConnectedFail() {

            }

            @Override
            public void onAbnormaldisconnection() {
                Toast.makeText(MainActivity.this, "打印机异常断开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onsateOFF() {
                Toast.makeText(MainActivity.this, "蓝牙关闭", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onsateOn() {
                Toast.makeText(MainActivity.this, "蓝牙开启", Toast.LENGTH_SHORT).show();
            }
        });
        Set<BluetoothDevice> pairedDevices =mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for (BluetoothDevice device : pairedDevices) {
                Log.i("device-bind",device.getName());
                if(device.getName().contains("QR")){
                    mBluetoothAdapter.cancelDiscovery();
                    printerPort.connect(device.getAddress());

                }
            }

        }else {
            Log.i("device-","no one");
        }
    }
    Bitmap bitmap;
    private Bitmap createCode(String content) {

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {

           bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 100, 100);
          //  binding.ivCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return  bitmap;

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter!= null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        printerPort.disconnect();
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            binding.tvCount.setText("打印数量："+msg.what);
            binding.tvOverplus.setText("剩余数量："+(bean.getSncount()-msg.what));

        }
    };
    int j=0;
    public void printeData() {



        new Thread(new Runnable() {
            @Override
            public void run() {
                printerPort.setDensity(0x02, 10);
                for (int i = bean.getBeginsn(); i < bean.getBeginsn()+bean.getSncount(); i++) {
                    j++;
                    printerPort.printBitmap(createCode(bean.getCCode()+"#"+bean.getInvstd()+"#"+(bean.getBeginsn()+i)));
                    printerPort.printerLocation(0x20,0);
                    handler.sendEmptyMessage(j);
                }


                if (printerPort.getSendResult(10000).equals("OK")) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.i("device-unbind",device.getName());
                    //  mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    };
}
