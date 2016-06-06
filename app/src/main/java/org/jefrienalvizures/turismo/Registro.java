package org.jefrienalvizures.turismo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jefrienalvizures.turismo.volley.WebService;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    TextView link;
    EditText usuarioTxt,correoTxt,nombreTxt,passTxt;
    Button btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        link = (TextView) findViewById(R.id.link_login);

        usuarioTxt = (EditText) findViewById(R.id.ususarioRegistro);
        passTxt = (EditText) findViewById(R.id.passRegistro);
        nombreTxt = (EditText) findViewById(R.id.nombreRegistro);
        correoTxt = (EditText) findViewById(R.id.correoRegistro);
        btnRegistro = (Button) findViewById(R.id.btnRegistro);

        btnRegistro.setOnClickListener(this);
        link.setOnClickListener(this);
    }

    private void registro(){
        if(!validar()){
            Toast.makeText(getBaseContext(),"Verifique los datos",Toast.LENGTH_SHORT).show();
            btnRegistro.setEnabled(true);
            return;
        }

        btnRegistro.setEnabled(false);
        final ProgressDialog pg = new ProgressDialog(this);
        pg.setIndeterminate(true);
        pg.setMessage("Registrando...");
        pg.setCancelable(false);
        pg.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        // INICIA REGISTRO

                        Map<String,String> params = new HashMap<String,String>();
                        params.put("usuario",usuarioTxt.getText().toString());
                        params.put("nombre",nombreTxt.getText().toString());
                        params.put("correo",correoTxt.getText().toString());
                        params.put("password",passTxt.getText().toString());

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WebService.registrar, new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    int estado = response.getInt("status");
                                    if(estado == 200){
                                        pg.dismiss();
                                        Toast.makeText(getBaseContext(),response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Registro.this,MainActivity.class));
                                        Registro.this.finish();
                                    } else {
                                        pg.dismiss();
                                        btnRegistro.setEnabled(true);
                                        Toast.makeText(getBaseContext(),response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                                    }
                                } catch(Exception ex){
                                    Log.e("Error",ex.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error response",error.getMessage());
                            }
                        });
                        WebService.getInstance(getBaseContext()).addToRequestQueue(request);
                        // FIALIZA REGISTRO
                    }
                }
        ,3000);
    }

    private boolean validar(){
        boolean respuesta = true;

        String u = usuarioTxt.getText().toString();
        String n = nombreTxt.getText().toString();
        String c = correoTxt.getText().toString();
        String p = passTxt.getText().toString();

        if(u.isEmpty() || u.length() < 3){
            usuarioTxt.setError("Minimo 3 caracteres!");
            respuesta = false;
        } else {
            usuarioTxt.setError(null);
        }
        if(n.isEmpty() || n.length() < 3){
            nombreTxt.setError("Minimo 3 caracteres!");
            respuesta = false;
        } else {
            nombreTxt.setError(null);
        }
        if(c.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(c).matches()){
            correoTxt.setError("Correo no valido!");
            respuesta = false;
        } else {
            correoTxt.setError(null);
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
        switch (v.getId()){
            case R.id.link_login:
                startActivity(new Intent(Registro.this,MainActivity.class));
                Registro.this.finish();
                break;
            case R.id.btnRegistro:
                registro();
                break;
        }
    }
}
