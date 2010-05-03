package fits.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sample extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button)findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startSub();
			}
		});
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//キーの押下時にメッセージダイアログを表示
    	Toast.makeText(this, "key press : " + keyCode, Toast.LENGTH_SHORT).show();
    	
    	return super.onKeyDown(keyCode, event);
    }

	private void startSub() {
		EditText et = (EditText)findViewById(R.id.EditText01);

        Intent intent = new Intent(this, SubAct.class);
        intent.putExtra("TEXT", et.getText());
        
        startActivity(intent);
	}
}