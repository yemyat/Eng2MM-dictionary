package qoodro.sg.eng2mm;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class ShowWord extends Activity {
	TextView textWord;
	TextView textState;
	TextView textDef;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		Typeface someFont = Typeface.createFromAsset(this.getAssets(), 
        "fonts/zawgyi.TTF"); 
		
		textWord = (TextView)findViewById(R.id.textWord);
		textState = (TextView)findViewById(R.id.textState);
		textDef = (TextView)findViewById(R.id.textDef);
		
		textWord.setTypeface(someFont);
		textState.setTypeface(someFont);
		textDef.setTypeface(someFont);
		
		Bundle myBundles = getIntent().getExtras();
		textWord.setText(myBundles.getString("word"));
		textState.setText(myBundles.getString("state"));
		textDef.setText(myBundles.getString("def"));
		
	}

}
