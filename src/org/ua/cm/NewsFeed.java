package org.ua.cm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NewsFeed {
	
	private List<NewsItem> itens;
	
	public NewsFeed() {
		itens = new LinkedList<NewsItem>();
	}
	
	public int addItem (NewsItem item) {
		this.itens.add(item);
		
		return this.itens.size();
	}
	
	public NewsItem getNews(int position) {
		return this.itens.get(position);
	}
	
	public List<NewsItem> getAllNews() {
		return this.itens;
	}
	
	public void retrieveNews() {
		
		try {
			
//			HttpClient client = new DefaultHttpClient();
//			HttpResponse response = client.execute(new HttpGet("http://uaonline.ua.pt/xml/contents_xml.asp"));
//			HttpEntity tmpEntity = response.getEntity();
			
//			SAXParser xmlParser = SAXParserFactory.newInstance().newSAXParser();
//			XMLReader xmlReader = xmlParser.getXMLReader();
//			NewsHandler newsHandler = new NewsHandler();
//			xmlReader.setContentHandler(newsHandler);
//
//			Reader test = new InputStreamReader(uaNewsURL.openStream());
//			InputSource is = new InputSource(test);
//			is.setEncoding("ISO-8859-1");
//			xmlReader.parse(is);
//			return newsHandler.getFeed();
			
			URL uaNewsURL = new URL("http://uaonline.ua.pt/xml/contents_xml.asp");
			DocumentBuilder xmlBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlNews = xmlBuilder.parse(uaNewsURL.openStream());
			
			XPath query;
			NodeList newsItems;
			query = XPathFactory.newInstance().newXPath();
			Object result = query.compile("/rss/channel/item").evaluate(xmlNews, XPathConstants.NODESET);
			newsItems = (NodeList) result;
				
			NewsItem tempItem;
			Element tmpElem;
			for (int i = 0; i < newsItems.getLength(); i++) {
				tmpElem = (Element)newsItems.item(i);
				
				tempItem = parseInfo(tmpElem);
				this.addItem(tempItem);
			}
			
			
		} catch (MalformedURLException e) {
			System.out.println("NewsFeed: retrieveNews: " + e.getMessage());
			System.out.println("NewsFeed: retrieveNews: MalformedURLException " + e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			System.out.println("NewsFeed: retrieveNews: " + e.getMessage());
			System.out.println("NewsFeed: retrieveNews: ParserConfigurationException " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println("NewsFeed: retrieveNews: " + e.getMessage());
			System.out.println("NewsFeed: retrieveNews: IOException " + e.getLocalizedMessage());
		} catch (XPathExpressionException e) {
			System.out.println("NewsFeed: retrieveNews: " + e.getMessage());
			System.out.println("NewsFeed: retrieveNews: XPathExpressionException " + e.getLocalizedMessage());
		} catch (SAXException e) {
			System.out.println("NewsFeed: retrieveNews: " + e.getMessage());
			System.out.println("NewsFeed: retrieveNews: SAXException " + e.getLocalizedMessage());
		}
	}
	
	private NewsItem parseInfo(Element item) {
		
		NewsItem tempItem = new NewsItem();
		
		tempItem.setTitle(item.getElementsByTagName("title").item(0).getTextContent());
		tempItem.setLink(item.getElementsByTagName("link").item(0).getTextContent());
		
		try {
			tempItem.setPubDate(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm z").parse(item.getElementsByTagName("pubDate").item(0).getTextContent().replaceAll("\\p{Cntrl}", "")));
		} catch (ParseException e) {
			System.out.println("NewsFeed: parseInfo Error parsing date-> " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		String tmpDesc = item.getElementsByTagName("description").item(0).getTextContent();
		int imgStartIndex = tmpDesc.indexOf("<img");
		System.out.println("Start of img tag: " + imgStartIndex);
		
		if (imgStartIndex != -1) {
			
			int imgStopIndex = tmpDesc.indexOf("/>",imgStartIndex);
			System.out.println("Sto of img tag: " + imgStopIndex);
			String imageTag = tmpDesc.substring(imgStartIndex+5, imgStopIndex-1);
			tmpDesc = tmpDesc.substring(imgStopIndex+2);
			imgStartIndex = imageTag.indexOf("src=\"");
			imgStopIndex = imageTag.indexOf("\" ", imgStartIndex);
			String imageSrc = imageTag.substring(imgStartIndex+5, imgStopIndex);
			System.out.println("Image Link: " + imageSrc);
			tempItem.setImage(imageSrc);
		}
		
		tempItem.setDescription(tmpDesc);
		
		return tempItem;
	}

}