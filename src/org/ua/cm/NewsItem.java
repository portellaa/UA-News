package org.ua.cm;

import java.util.Date;
	
public class NewsItem {
		
	private String title;
	private String link;
	private String description;
	private String image;
	private Date pubDate;
	
	public NewsItem() {
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		if (title.length() > 40)
			return (title.substring(0, 40) + " ...");
		return title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
}
