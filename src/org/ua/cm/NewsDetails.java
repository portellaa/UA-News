package org.ua.cm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsDetails extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		loadLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_details);
		
		Bundle receivedParams = this.getIntent().getExtras();
		
		if (receivedParams != null) {
			
			fillData(receivedParams.getString("title"), receivedParams.getString("description"), receivedParams.getString("pubDate"), 
					receivedParams.getString("link"), receivedParams.getString("image"));
			
		}
		else this.finish();
		
	}
	
	@Override
    protected void onResume() {
    	loadLocale();
    	super.onResume();
    }
	
	private void fillData(String title, String desc, String pubDate, String link, String imageLink) {
		
		TextView titleTxt = (TextView)findViewById(R.id.newsTitle);
		TextView descTxt = (TextView)findViewById(R.id.newsDescription);
		TextView pubDateTxt = (TextView)findViewById(R.id.newsPubDate);
		TextView fullArticle = (TextView)findViewById(R.id.newsArticle);
		ImageView image = (ImageView)findViewById(R.id.newsImage);
		
		titleTxt.setText(title);
		descTxt.setText(desc);
		pubDateTxt.setText(pubDate);
		fullArticle.setText(Html.fromHtml("<a href=\"" + link + "\">Ver Artigo Completo"+"</a>"));
		fullArticle.setMovementMethod(LinkMovementMethod.getInstance());
		
		try {
			Drawable imageDra = Drawable.createFromStream(new URL(imageLink).openStream(), "newsImage");
			image.setImageDrawable(imageDra);
		} catch (MalformedURLException e) {
			System.out.println("NewsDetails: fillData: " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println("NewsDetails: fillData: " + e.getLocalizedMessage());
		}
		
		
	}
	
	private void loadLocale() {
    	Configuration tmpConfig = new Configuration();
		tmpConfig.locale = new Locale(PreferenceManager.getDefaultSharedPreferences(this).getString("listIdioma", ""));
		getResources().updateConfiguration(tmpConfig, null);
    }

}
