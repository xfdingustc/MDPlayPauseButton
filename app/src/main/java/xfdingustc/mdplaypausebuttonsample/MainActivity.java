package xfdingustc.mdplaypausebuttonsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xfdingustc.mdplaypausebutton.PlayPauseButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PlayPauseButton view = (PlayPauseButton) findViewById(R.id.btn_play_pause);
        view.toggle(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.toggle(!view.isShowingPlay());
            }
        });
    }
}
