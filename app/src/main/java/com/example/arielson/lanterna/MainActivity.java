package com.example.arielson.lanterna;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    /*
     * Controla a comunicação com a camera
     */
    private Camera cam;
    /**
     * Parâmetros
     */
    private Camera.Parameters parameters;

    private boolean isLedOn;
    private ImageButton btn;
    private ImageView lamp;
    private final int REQUEST_CAM = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAM);
        }else
            getCamera();
        setContentView(R.layout.activity_main);


        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Atenção");
            alerta.setMessage("Seu dispositivo não possui Flash");
            alerta.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alerta.show();
            return;
        }
        btn = (ImageButton) findViewById(R.id.btn_on_off);
        lamp = (ImageView) findViewById(R.id.imv_status);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ligarAndDesligar();
            }
        });
        btn.setContentDescription(getString(R.string.ativar_lant));
        lamp.setContentDescription(getString(R.string.lant_inativo));

    }

    /**
     * Obtêm o acesso a camera para ativar o led e obtêm os parametros
     * Abre a comunicação com a camera
     */
    private void getCamera() {

        if (cam == null) {
            cam = Camera.open();
            parameters = cam.getParameters();
        }
    }

    /**
     * Ligar ou desliga o led do flash
     */
    private void ligarAndDesligar() {
        if (!isLedOn) {
            if (cam == null || parameters == null)
                return;
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(parameters);
            cam.startPreview();
            isLedOn = true;
        } else {
            if (cam == null || parameters == null)
                return;
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(parameters);
            cam.stopPreview();
            isLedOn = false;
        }
        changeIconAndContentDescription();
    }

    /**
     * Alterar o icone do Button e o imageView
     */
    private void changeIconAndContentDescription() {
        if (isLedOn) {
            btn.setImageResource(R.drawable.bt_on);
            lamp.setImageResource(R.drawable.lamp_on);
            btn.setContentDescription(getString(R.string.desativar_lant));
            lamp.setContentDescription(getString(R.string.lant_ativo));
        } else {
            btn.setImageResource(R.drawable.bt_off);
            lamp.setImageResource(R.drawable.lamp_off);
            btn.setContentDescription(getString(R.string.ativar_lant));
            lamp.setContentDescription(getString(R.string.lant_inativo));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAM);
        }else
            getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**
         * Encerra a comunicação com a camera
         */
        if (cam != null) {
            cam.release();
            cam = null;
            isLedOn = false;
            changeIconAndContentDescription();
        }
    }

    @Override
    public void finish() {
        super.finish();
        /**
         * Encerra a comunicação com a camera
         */
        if (cam != null) {
            cam.release();
            cam = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case  REQUEST_CAM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getCamera();
                else
                    finish();
                return;
        }
    }
}
