package com.example.mihael.scanapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeActivity extends AppCompatActivity {

    private static final String TAG = "QrCodeActivity";
    private ProgressBar progressBar;
    private String productInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        showProgressbar();

        //get barcode
        String barcode = getIntent().getStringExtra("barcode");
        Log.i(TAG, "barcode: " + barcode);
        TextView bar = (TextView)findViewById(R.id.barcode);
        bar.setText(barcode);

        //get data for barcode
        productInfo = getDataFromDB(barcode);
        Log.i(TAG, "productInfo: " + productInfo);
        String productInfo2 = productInfo.replaceAll("\\|", " ");
        TextView info = (TextView)findViewById(R.id.info);
        info.setText(productInfo2);

        //create qrCode for specified barcode and show to user
        if (productInfo != null)
            createQrCode(productInfo);
        else {
            Toast.makeText(this, "We do not recognize this barcode, please scan again.",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BarcodeScanActivity.class);
            startActivity(intent);
        }
        hideProgressBar();
    }

    private String getDataFromDB(String barcode) {
        //TODO [Mihael]: replace with SELECT from ....
        DBImmitator dbImmitator = DBImmitator.getInstance();
        return dbImmitator.get(barcode);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressbar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }


    private void createQrCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void validate(View source) {
        Intent intent = new Intent(this, QrCodeScanActivity.class);
        intent.putExtra("productInfo", productInfo);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String qrCodeScanned = data.getStringExtra("qrCodeScanned");
                Log.i(TAG, "Code scanned: " + qrCodeScanned);
                Log.i(TAG, "Code generated: " + productInfo);

                if (qrCodeScanned.equals(productInfo)) {
                    //startActivity with true result
                    Log.i(TAG, "Starting activity with true result");

                   Intent intent = new Intent(this, ResultActivity.class);
                  intent.putExtra("result", true);
                  startActivity(intent);
                }
                else {
                    //startActivity with false
                    Log.i(TAG, "Starting activity with false result");

                   Intent intent = new Intent(this, ResultActivity.class);
                 intent.putExtra("result", false);
                 startActivity(intent);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
