package com.xxzj990.android.parkingassistline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private SupportLineView supportLineView;
    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportLineView = (SupportLineView) findViewById(R.id.support_line);
        edit = (Button) findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(supportLineView.getMode()== SupportLineView.Mode.EDIT) {
                    supportLineView.setMode(SupportLineView.Mode.VIEW);
                    edit.setText(R.string.edit);
                } else {
                    supportLineView.setMode(SupportLineView.Mode.EDIT);
                    edit.setText(R.string.view);
                    supportLineView.saveData();
                }
            }
        });
    }
}
