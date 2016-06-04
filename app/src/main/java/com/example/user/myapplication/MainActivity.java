package com.example.user.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.StrictMode;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.Toast;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;

/*
	This source code could be used for academic purposes only. Posting on other websites or blogs is only allowed with a dofollow link to the orignal content.
*/

public class MainActivity extends Activity
{

    Button connect_button;
    EditText sql_server_ip, sql_login, sql_password, abonent_id;
    ProgressBar progressBar;

    Connection con;
    String username,password,database,ip,abonent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sql_server_ip = (EditText) findViewById(R.id.SQL_Server_IP);
        sql_login = (EditText) findViewById(R.id.SQL_Login);
        sql_password = (EditText) findViewById(R.id.SQL_Password);
        abonent_id = (EditText) findViewById(R.id.Abonent_ID);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        connect_button = (Button) findViewById(R.id.Connect_Button);

        connect_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ip = sql_server_ip.getText().toString();
                username = sql_login.getText().toString();
                password = sql_password.getText().toString();
                abonent = abonent_id.getText().toString();
                database = "Abonents";
                CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
                checkLogin.execute(abonent);               //Запуск потока с подключением к бд
            }
        });
    }


    public class CheckLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        String s1="";
        String s2="";
        String s3="";
        String s4="";
        String s5="";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);                    //Запуск прогресс бара для индикации работы приложения
        }


        @Override
        protected String doInBackground(String... params)
        {
            int cnt = 0;
            if(abonent.trim().equals("")||ip.trim().equals("")||password.trim().equals("")||database.trim().equals(""))
                z = "Заполните все поля";
            else
            {
                try
                {
                    con = connectToSQL(username, password, database, ip);        // Connect to database
                    if (con == null)
                    {
                        z = "Не удается соединиться с MSSQL сервером!";
                    }
                    else
                    {
                        String query = "select * from A_Table where Abonent_id= '" + abonent +"' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            z = "Есть такой абонент!";
                            s1=rs.getString(1);
                            s2=rs.getString(2);
                            s3=rs.getString(3);
                            s4=rs.getString(4);
                            s5=rs.getString(5);
                            publishProgress(s1,s2,s3,s4,s5);
                            isSuccess=true;
                            con.close();
                        }
                        else
                        {
                            z = "Нет такого абонента!";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(MainActivity.this , values[0]+" "+values[1]+" "+values[2]+" "+values[3]+" "+values[4], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);                       //Отключение прогресс бара
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show(); //Показать сообщение с результатом работы
            if(isSuccess)
            {
                Toast.makeText(MainActivity.this , "Всё ок" , Toast.LENGTH_LONG).show(); //Если всё прошло успешно, то показать ещё одно сообщение
                //finish();
            }
        }
    }


    @SuppressLint("NewApi") //хз зачем это тут
    public Connection connectToSQL(String user, String password, String database, String server)         //метод для установки соединения с MSSQL базой
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server +"/" + database + ";user=" + user+ ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}
