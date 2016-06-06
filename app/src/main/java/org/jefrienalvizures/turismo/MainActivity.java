package org.jefrienalvizures.turismo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jefrienalvizures.turismo.bean.Usuario;
import org.jefrienalvizures.turismo.volley.WebService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //INPUTS
    TextInputLayout usuarioTxt,passTxt;
    TextView link;
    Button btnLlogin;
    private Usuario userLogged = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioTxt = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passTxt = (TextInputLayout) findViewById(R.id.passwordWrapper);

        usuarioTxt.setHint("Nombre de usuario");
        passTxt.setHint("Contraseña");

        link = (TextView) findViewById(R.id.link_registro);
        link.setOnClickListener(this);
        btnLlogin = (Button) findViewById(R.id.btnLogin);
        btnLlogin.setOnClickListener(this);

    }

    public void log(View v){

        if(!estaConectado()){
            return;
        }

        if(!validar()){
            Toast.makeText(getBaseContext(),"Verifique sus credenciales",Toast.LENGTH_SHORT).show();
            btnLlogin.setEnabled(true);
            return;
        }

        btnLlogin.setEnabled(false);

        final ProgressDialog pg = new ProgressDialog(MainActivity.this);
        pg.setIndeterminate(true);
        pg.setMessage("Iniciando Sesión...");
        pg.setCancelable(false);
        pg.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //INICIA PROCESO DE LOGIN

                        Map<String,String> params = new HashMap<String,String>();
                        params.put("usuario",usuarioTxt.getEditText().getText().toString());
                        params.put("password",passTxt.getEditText().getText().toString());
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WebService.autenticar, new JSONObject(params), new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    JSONArray listaUsuario =  response.getJSONArray("user");
                                    if(listaUsuario.length()>0){
                                        JSONObject user = listaUsuario.getJSONObject(0);

                                        userLogged = new Usuario(
                                                user.getInt("id"),
                                                user.getString("usuario"),
                                                user.getString("nombre"),
                                                user.getString("correo"),
                                                response.getString("token"),
                                                response.getString("exp")
                                        );
                                        pg.dismiss();
                                        Toast.makeText(getApplicationContext(),"Bienvenido "+user.getString("nombre"),Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(MainActivity.this,Inicio.class));
                                        MainActivity.this.finish();
                                    } else {
                                        pg.dismiss();
                                        btnLlogin.setEnabled(true);
                                        Toast.makeText(getApplicationContext(),"Verifique sus credenciales",Toast.LENGTH_LONG).show();
                                    }
                                } catch(Exception ex) {
                                    Log.e("Error",ex.getMessage());
                                }
                            }
                        }, new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error Response",error.getMessage());
                            }
                        });
                        WebService.getInstance(getBaseContext()).addToRequestQueue(request);

                        //FINALIZA PROCESO DE LOGIN
                    }
                }
        ,3000);


    }

    private boolean validar(){
        boolean respuesta = true;

        String u = usuarioTxt.getEditText().getText().toString();
        String p = passTxt.getEditText().getText().toString();

        if(u.isEmpty() || u.length() < 3){
            usuarioTxt.setError("Minimo 3 caracteres!");
            respuesta = false;
        } else {
            usuarioTxt.setError(null);
        }
        if(p.isEmpty() || p.length() < 3){
            passTxt.setError("Minimo 3 caracteres!");
            respuesta = false;
        } else {
            passTxt.setError(null);
        }
        return respuesta;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLogin:
                log(v);
                break;
            case R.id.link_registro:
                startActivity(new Intent(MainActivity.this,Registro.class));
                MainActivity.this.finish();
                break;
        }
    }


    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            if(conectadoRedMovil()){
                return true;
            }else{
                showAlertDialog(MainActivity.this, "Conexion a Internet",
                        "Tu Dispositivo no tiene Conexion a Internet.", false);
                return false;
            }
        }
    }


    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = alertDialog.create();

        alert.show();
    }


}