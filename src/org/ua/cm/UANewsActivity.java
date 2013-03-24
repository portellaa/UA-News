package org.ua.cm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class UANewsActivity extends ListActivity {

	NewsFeed newsFeed = new NewsFeed();
	
	final int NEWS_TITLE_SIZE = 65;
	final int NEWS_DESC_SIZE = 100;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (checkInternetAvailable()) {
	        final ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.gettingInfo), true);
			progressDialog.show();
			new Thread() {
				public void run() {
					newsFeed.retrieveNews();
					runOnUiThread(new Runnable() {
	    			    public void run() {
	    			    	setListAdapter(new NewsItemRowAdapter(UANewsActivity.this, newsFeed.getAllNews()));
	    			    }
	    			});
					progressDialog.dismiss();
				}
			}.start();
        }
        else {
        	buildAlertNoInternetConnectivity();
        }
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		Intent newsDetails = new Intent(this, NewsDetails.class);
		Bundle newsDetailsParams = new Bundle();
		
		newsDetailsParams.putString("title", newsFeed.getNews(position).getTitle());
		newsDetailsParams.putString("description", newsFeed.getNews(position).getDescription());
		newsDetailsParams.putString("image", newsFeed.getNews(position).getImage());
		newsDetailsParams.putString("pubDate", new SimpleDateFormat("EEEE, dd MMMM").format(newsFeed.getNews(position).getPubDate()));
		newsDetailsParams.putString("link", newsFeed.getNews(position).getLink());
		
		newsDetails.putExtras(newsDetailsParams);
		startActivity(newsDetails);
		
	}
	
	protected class NewsItemRowAdapter extends ArrayAdapter<NewsItem> {
		
		private final Activity con;
		private final List<NewsItem> item;
		
		private int[] colors = new int[] { 0x00000000, 0xAA538d00 };
		
		public NewsItemRowAdapter(Activity con, List<NewsItem> item) {
			super(con, R.layout.news_item_row, item);
			
			this.con = con;
			this.item = item;
			
			System.out.println("[NewsItemRowAdapter] : Numero de linhas -> " + item.size());
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = this.con.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.news_item_row, null, true);
			
			ImageView newsImageRow = (ImageView) rowView.findViewById(R.id.newsImageRow);
			TextView newsTitleRow = (TextView) rowView.findViewById(R.id.newsTitleRow);
			TextView newsDescRow = (TextView) rowView.findViewById(R.id.newsDescRow);
			TextView newsPubDateRow = (TextView) rowView.findViewById(R.id.newsPubDateRow);
			RatingBar newsRattingRow = (RatingBar) rowView.findViewById(R.id.newsRatingBar);
			
			NewsItem tmpItem = this.item.get(position);
			
			try {
				Drawable imageDra = Drawable.createFromStream(new URL(tmpItem.getImage()).openStream(), "newsImage");
				newsImageRow.setImageDrawable(imageDra);
			} catch (MalformedURLException e) {
				newsImageRow.setImageResource(R.drawable.icon_ua);
			} catch (IOException e) {
				newsImageRow.setImageResource(R.drawable.icon_ua);
			}
			
			// Setting News Row Title
			String tmpTitle = tmpItem.getTitle().toUpperCase();
			if (tmpTitle.length() > NEWS_TITLE_SIZE) {
				tmpTitle = tmpTitle.substring(0, NEWS_TITLE_SIZE);
				tmpTitle = tmpTitle.substring(0, tmpTitle.lastIndexOf(' '));
				tmpTitle = tmpTitle + " ...";
			}
			
			newsTitleRow.setText(tmpTitle);
			
			// Setting News Row Description
			String tmpDescription = tmpItem.getDescription().toString();
			if (tmpDescription.length() > NEWS_DESC_SIZE) {
				tmpDescription = tmpDescription.substring(0, NEWS_DESC_SIZE);
				tmpDescription = tmpDescription.substring(0, tmpDescription.lastIndexOf(' '));
				tmpDescription = tmpDescription + " ...";
			}
			
			newsDescRow.setText(tmpDescription);
			
			// Setting News Row Description
			newsPubDateRow.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(tmpItem.getPubDate()));
			
			// Setting random stars
			newsRattingRow.setRating((float)(Math.random() * 5.0));
			
//			rowView.setBackgroundColor(Color.rgb(colors[position % 2][0], colors[position % 2][1], colors[position % 2][2]));
			rowView.setBackgroundColor(colors[position % 2]);

			return rowView;
		}
	}
	
	private void buildAlertNoInternetConnectivity() {
    	
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.internetAlertTitle);
		alert.setMessage(R.string.noInternetMessage);
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				finish();
				return;
			}
		});
		alert.show();
    }
	
	public boolean checkInternetAvailable(){
		return (((ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null);
	}
}