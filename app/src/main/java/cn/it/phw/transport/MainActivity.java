package cn.it.phw.transport;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btn1;
    private TextView tv1;

    private Handler mHandler = new Handler(message -> {
        switch (message.what) {
            case 0: {
                tv1.setText( (String) message.obj);
                break;
            }
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        tv1 = findViewById(R.id.tv);

        btn1.setOnClickListener((view) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("CarId", 1);
            /*HttpUtils utils = new HttpUtils(HttpUtils.SERVER + "GetCarSpeed.do");
            tv1.setText(utils.post(map));*/
            HttpUtils utils = new HttpUtils(HttpUtils.SERVER + "GetCarSpeed.do", mHandler);
            utils.postByHandler(map);
        });
    }



}
