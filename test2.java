package twitter;

import twitter4j.*;
import java.io.*;
import twitter4j.conf.ConfigurationBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.net.MalformedURLException;
import org.jsoup.*;
import org.jsoup.nodes.Document;

public class test2 
{

	public static void main(String[] args) throws TwitterException, IOException{
	{
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey("PpAmXRWYREfx0RW7hDTZmG7Mn")
			.setOAuthConsumerSecret("thS7IICHAGcaKdjsfM32slX66WFD1LNC2vvk6y9o6QfRZSU582")
			.setOAuthAccessToken("805495253469757440-MGSTzgeWhUFOZKQIBPX9o0qPPh5elnf")
			.setOAuthAccessTokenSecret("otIvyioCf58H7PKbpgj8tfjROfVKUeQQFSqP3eB44OMkE");
		
		
		
		StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	
//            	if(status.getLang().contains("en") && status.getPlace() != null && !status.getPlace().getCountry().contains("?"))
            	if(status.getLang().contains("en") && status.getGeoLocation() != null )
        		//filters out non english tweets and non geolocated tweets
            	{
	            	String tw = status.getText();
	            	String tw2 = tw;
	            	int index = 0;
	            	String hyperlink = "";
	            	String hashtag = "";
	            	String otherUser = "";
	            	String author = status.getUser().getScreenName();
	            	List <String> links = new ArrayList<String>(); 
	            	List <String> hashtags = new ArrayList<String>();
	            	List <String> ats = new ArrayList<String>();
	            	
            		System.out.println("@" + author + " - " + tw);
            		System.out.println("      " + status.getGeoLocation());
            		double latitude = status.getGeoLocation().getLatitude();
            		double longitude = status.getGeoLocation().getLongitude();
            		
	            	while(tw2.length() > 0) 		//parses the tweet
	            	{
	            		int h = 	tw2.indexOf("http");
	            		int amp = 	tw2.indexOf("#");
	            		int at = 	tw2.indexOf("@");
	            		if (h == -1)
	            			h = 1000;
	            		if(amp == -1)
	            			amp = 1000;
	            		if(at == -1)
	            			at = 1000;
	            		
	            		if(h < amp && h < at ) //link
	            		{
	            			index = tw2.length();
	            			if(tw2.substring(h).contains(" "))
	            				index = tw2.indexOf(" ", h);
	            			hyperlink = tw2.substring(h, index);
		            		try 
		            		{
		            			URL url = new URL(hyperlink);
		            			if(url.getHost() != null)
		            			{
		            				Document doc;
		            				try	{
		            					doc = Jsoup.connect(hyperlink).get();
		            					links.add(doc.title());
		            					/*
		            					 * I parsed the title in a previous iteration
		            					 * Check the bottom of the file for an example
		            					 * Although there might be a better way using jsoup
		            					 */
		            				}
		            				catch(Exception e)
	            					{System.err.println("Unable to connect using Jsoup.");}
		            			}
		            		}
		            		catch(MalformedURLException e) 
		            			{System.err.println("Unable to fetch URL.");}
		            		
		            		tw2 = tw2.substring(index,tw2.length());
	            		}
	            		else if(amp < at ) 		//#
	            		{
	            			index = tw2.length();
	            			if(tw2.substring(amp).contains(" "))
	            				index = tw2.indexOf(" ", amp);
	            			if( (amp+1) != index)				// this is to prevent a non hashtag # from being added
	            			{	
		            			hashtag = tw2.substring(amp, index);
			            		hashtags.add(hashtag);
			            		tw2 = tw2.substring(index,tw2.length());
	            			}
	            		}
	            		else if(at < amp)		//@
	            		{
	            			index = tw2.length();
	            			if(tw2.substring(at).contains(" "))
	            				index = tw2.indexOf(" ", at);
	            			if( (at+1) != index)				// this is to prevent a non @<user> @ from being added
	            			{
		            			otherUser = tw2.substring(at, index);
			            		ats.add(otherUser);
			            		tw2 = tw2.substring(index,tw2.length());
	            			}
	            		}
	            		else if(h == amp && amp == at && amp == 1000)
	            			break;
	            		else
	            			System.err.println("Error with parsing/index of substrings");
	            	}
            
	            	if(links.size() > 0)
	            		System.out.println("      Links " + links);
	            	if(hashtags.size() > 0)
	            		System.out.println("      Hashtags " + hashtags);
	            	if(ats.size() > 0)
	            		System.out.println("      Ats " + ats);
	            	
	            	String loc = status.getPlace().getFullName();
	            	String city = status.getPlace().getName();
	            	String state = loc.substring(loc.indexOf(", ")+2); 	//I'm not sure if it's better to separate these or not
	            	String country = status.getPlace().getCountry();
//            		System.err.println("City: " + city + "\nState: " + state + "\nCountry: " + country + "\n");
            		// Can be redundant, sometimes the city = state, sometimes the state = country

            		/*
            		 * Things to store in the JSON:
            		 * 
            		 * author						// The one who wrote the tweet
            		 * tw 							// the string of the tweet
            		 * links 						// This is a list of the URLs in a single tweet that need to be crawled 
            		 * 								// If the should also get the title of the page while crawling, and remove the real time title capture 
            		 * hashtags						// A list of the hashtags in a single tweet
            		 * ats 							// A list of people @ in a tweet
            		 * city,state,country 			// Might not need this
            		 * latitude and longitude
            		 * */
            	
        		}
          
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
//                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
//                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
		    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		    twitterStream.addListener(listener);
		    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
//		    FilterQuery tweetFilterQuery = new FilterQuery(); // See 
//		    tweetFilterQuery.language(new String[]{"en"}); 	
//		    twitterStream.filter(tweetFilterQuery);
		    twitterStream.sample();
		}
	}
}

//while(tw2.contains("http") && !tw2.contains("…") ) 		//… caused an infinite loop
//{
//	linkIndex = tw2.indexOf("http");
//	if(tw2.substring(linkIndex).contains(" "))
//	{
//		linkIndex2 = tw2.indexOf(" ", linkIndex);
//	}
//	else
//	{
//		linkIndex2 = tw2.length();
//	}
//	
//	hyperlink = tw2.substring(linkIndex, linkIndex2);
//	try{
//		URL url = new URL(hyperlink);
//		if(url.getHost() != null)
//		{
//			Document doc;
//			try
//			{
//				doc = Jsoup.connect(hyperlink).get();
////				System.out.println("URL: " + doc.title());
//				/*
//				 * Here is where you would use jsoup to crawl the webpage for links and such
//				 * You can read about this on the TA's week 4 discussion slide 11
//				 * There might be a problem with doing this in real time
//				 */
//				links.add(doc.title());
//				URL url2 = new URL(doc.title());
//				InputStream is = url2.openStream();
//				int ptr = 0;
//				StringBuffer buffer = new StringBuffer();
//				while((ptr = is.read()) != -1)
//				{
//					buffer.append((char)ptr);
//					if(buffer.indexOf("</title>") != -1 || buffer.indexOf("</head>") != -1)
//						break;
//				}
//				is.close();
//				try {
//					int titleIndex = buffer.indexOf("<title>");
//					int titleIndex2 = buffer.indexOf("</title>");
//					if( titleIndex != -1 && titleIndex2 != -1)
//					{
//							System.out.println("  >Title: " + buffer.substring(titleIndex+7,titleIndex2));
//							/*
//							 * This probably shouldn't be done in real time and should probably be moved
//							 * 
//							 * Also there might be a better way to do this then the while loop and the indexes by using jsoup
//							 */
//							
//					}
//					else
//						System.out.println("No title found");
//					// There is a problem with the title and html unicode example " &#10; " should be the newline
//					// Not sure if this needs to be fixed or not
//					// https://stackoverflow.com/questions/27791430/how-to-convert-string-with-html-encoding-to-unicode-in-java
//				}
//				catch(Exception e)
//				{
//					System.err.println("Failed to extract title of URL:\n   " + doc.title());
//				}
//			}
//			catch(Exception e )
//			{
//				System.err.println("Unable to fetch title.");
//			}
//		}
//		else
//			System.err.println("URL host is null");
//	}
//	catch(MalformedURLException e) 
//	{continue;}
//	tw2 = tw2.substring(linkIndex2,tw2.length());
//}
