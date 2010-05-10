package qoodro.sg.eng2mm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Dictionary extends Activity {
	TextView textCaption;
	EditText editQuery;
	Button btnSearch;
	HttpClient httpClient;
	HttpGet request;
	HttpResponse response;
	BufferedReader reader;
	ListView listResults;
	ArrayList<String> words = new ArrayList<String>();
	ArrayList<String> states = new ArrayList<String>();
	ArrayList<String> meanings = new ArrayList<String>();
	ArrayAdapter<String> myAdapter;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textCaption = (TextView)findViewById(R.id.textCaption);
		editQuery = (EditText)findViewById(R.id.editQuery);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		listResults = (ListView)findViewById(R.id.listResults);

		myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,words);
		listResults.setAdapter(myAdapter);

		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(editQuery.getText().length()>0){
					ProgressDialog dialog = ProgressDialog.show(Dictionary.this, "", 
							"Loading. Please wait...", true);
					
					words.clear();
					states.clear();
					meanings.clear();
					getRelatedWords(editQuery.getText().toString());
					dialog.dismiss();
				}else{
					Toast.makeText(getBaseContext(), "Invalid input",Toast.LENGTH_SHORT).show();
				}
			}
		});

		listResults.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Object[] myWords = words.toArray();
				Object[] myStates = states.toArray();
				Object[] myDefs = meanings.toArray();
				Intent myIntent = new Intent(arg1.getContext(), ShowWord.class);
				Bundle dataBun = new Bundle();
				dataBun.putString("word",myWords[arg2].toString());
				dataBun.putString("state",myStates[arg2].toString());
				dataBun.putString("def",myDefs[arg2].toString());
				myIntent.putExtras(dataBun);
				startActivityForResult(myIntent, 0);
			}

		});
	}
	private void getRelatedWords(String queryWord) {
		String tempUrl = "http://www.ornagai.com/index.php/api/word/q/"+queryWord;
		URLConnection myConnection;

		try {
			
			//open connection to server
			URL serverUrl = new URL(tempUrl);
			myConnection = serverUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)myConnection;
			
			//get response code
			int responseCode = httpConnection.getResponseCode();

			//HTTP400
			if(responseCode == HttpURLConnection.HTTP_OK) {
				//get input stream
				InputStream input = httpConnection.getInputStream();
				
				//change that stream into a document
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(input);
				Element docEle = doc.getDocumentElement();
				
				//parsing
				NodeList nl = docEle.getElementsByTagName("item");
				if(nl != null && nl.getLength() > 0){
					for(int i = 0;i<nl.getLength();i++){
						Element item = (Element)nl.item(i);
						Element word = (Element)item.getElementsByTagName("word").item(0);
						Element state = (Element)item.getElementsByTagName("state").item(0);
						Element def = (Element)item.getElementsByTagName("def").item(0);
						String myWord = word.getFirstChild().getNodeValue();
						String myState = state.getFirstChild().getNodeValue();
						String myDef = def.getFirstChild().getNodeValue();
						words.add(myWord);
						states.add(myState);
						meanings.add(myDef);
					}
					textCaption.setText("Related words");
					myAdapter.notifyDataSetChanged();
				}
			} else {
				Toast.makeText(getBaseContext(), "No results found",Toast.LENGTH_SHORT).show();
				editQuery.setText("");
				textCaption.setText("");
			}

		} catch (MalformedURLException e) {
			alertUser("Error in the web service");
		} catch (IOException e) {
			alertUser("No access to the internet");
			e.printStackTrace();
		} catch (SAXException e) {
			alertUser("Error parsing the XML response");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			alertUser("Error parsing the XML response");
			e.printStackTrace();
		} 
	}
	private void alertUser(String message) {
		new AlertDialog.Builder(this).setTitle("Alert!").setMessage(message).setNeutralButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dlg, int sumthin) {
				// do nothing – it will close on its own
			}
		}).show();
	}
}