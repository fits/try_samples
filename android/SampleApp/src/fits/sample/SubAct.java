package fits.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubAct extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subact);
        
        Button button = (Button)findViewById(R.id.Button02);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
        
        TextView tv = (TextView)findViewById(R.id.TextView02);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	CharSequence text = extras.getCharSequence("TEXT");
        	tv.setText(text);
        }
    }
}
