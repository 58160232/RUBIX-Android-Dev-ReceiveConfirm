package com.example.wiroon.test1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.widget.Toast;

import com.example.wiroon.test1.Fragment.LoginFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class login extends AppCompatActivity implements LoginFragment.OnClickLogin {
    String URL = "";
    String CompanyCode = "";
    FragmentTransaction transaction;
    AsynctaskAuthen Init;
    Fragment temp;
    ConnectivityManager cm;
    int Frag;
    private AlertDialog.Builder alert, internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadConfig();
        //default fragment from start app
        if (savedInstanceState == null) {
            Frag = 0;
            temp = new LoginFragment();
            transaction = getSupportFragmentManager().beginTransaction().replace(R.id.FragmentLogin,temp);
            transaction.commit();
        }
    }

    public static String getXmlTag(String tag, String content) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        String xml = "";
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(content));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && tag.equals(xpp.getName())) {
                eventType = xpp.next();
                xml = xpp.getText();
            }
            eventType = xpp.next();
        }
        return xml;
    }

    @Override
    public void Login(String user, String pass) {
        if (!Appconfig.checkstate()) loadConfig();
        String checkLogin = "";
        LoginFragment clearuser = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentLogin);
        try {
            JSONObject job = new JSONObject();
            job.accumulate("Username", user);
            job.accumulate("Password", pass);
            if (!Appconfig.checkstate()) {
                Toast.makeText(this, "please check setting", Toast.LENGTH_SHORT).show();
                return;
            }
            Appconfig.setContext(this);
            AsyncTaskAdapter sr = new AsyncTaskAdapter(job);
            sr.execute("api/UserMaintenance/VerifyUser");
            checkLogin = sr.get(10000, TimeUnit.MILLISECONDS).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(this, "Please check internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkLogin.equals("true")) {
            Appconfig.setCurrentUser(user);
            Intent i_main = new Intent(this, MainActivity.class);
            Appconfig.context = null;
//            i_main.putExtra("Appconfig", (Serializable) null);
            startActivity(i_main);
            this.finish();
        } else if (checkLogin.equals("false")) {
            alert = new AlertDialog.Builder(this);
            alert.setMessage("Invalid Username or Password!").setNegativeButton("OK", null).show();
            clearuser.wrong();
            clearuser.onDestroy();
            alert = null;
        }


    }
    public void loadConfig() {
        try {
            FileInputStream fileIn = openFileInput("config01.xml");
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[150];
            String s = "";
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
            URL = getXmlTag("WebAuthenUrl", s);
            CompanyCode = getXmlTag("CompanyCode", s);
            Init = new AsynctaskAuthen(new Gson().toJson(CompanyCode));
            Init.execute(URL + "api/Registration/LoadDatabaseConfigByCompanyCode");
            Init.get(3, TimeUnit.SECONDS);

        } catch (IOException e) {
            creatConfig();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(this, "Please check internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void creatConfig() {
        final String xmlFile = "config01.xml";
        try {
            FileOutputStream fileos = openFileOutput(xmlFile, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileos);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            xmlSerializer.startTag(null, "WebAuthenUrl");
            xmlSerializer.text("http://192.168.128.249/RUBIXBasePocAuthentication/");
            xmlSerializer.endTag(null, "WebAuthenUrl");

            xmlSerializer.startTag(null, "CompanyCode");
            xmlSerializer.text("TR-TEST");
            xmlSerializer.endTag(null, "CompanyCode");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            outputWriter.write(writer.toString());
            outputWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
