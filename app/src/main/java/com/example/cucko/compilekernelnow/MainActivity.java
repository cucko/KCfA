package com.example.cucko.compilekernelnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    InputStream in;
    BufferedReader reader;
    String line;
    TextView text;
    TextView status;
    String out = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.tvOut);
        status = (TextView) findViewById(R.id.tvStatus);
        text.setMovementMethod(new ScrollingMovementMethod());
    }


    public void readLine() {
        try {
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (line != null) {
            out += "\n" + line;
            text.setText(out);
            final int scrollAmount = text.getLayout().getLineTop(text.getLineCount()) - text.getHeight();
            // if there is no need to scroll, scrollAmount will be <=0
            if (scrollAmount > 0)
                text.scrollTo(0, scrollAmount);
            else
                text.scrollTo(0, 0);

            text.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    text.scrollTo(0, Integer.MAX_VALUE);
                    readLine();
                }
            }, new Random().nextInt(30));
        } else {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            status.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            ((Button) findViewById(R.id.btnCompile)).setText("Compile Again");
        }

    }

    public void onCompile(View view) {
        try {
            text.setText("");
            out = "";
            ((Button) findViewById(R.id.btnCompile)).setText("Compiling ...");
            status.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            in = this.getAssets().open("kernel" + (new Random().nextInt(4) + 1) + ".txt");
            reader = new BufferedReader(new InputStreamReader(in));
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line != null)
            out += line;

        text.setText(out);

        readLine();
    }
}
