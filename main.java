package twitter;

import twitter4j.*;
import java.io.*;
import twitter4j.conf.ConfigurationBuilder;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.MalformedURLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class main 
{

	public static void main(String[] args) throws TwitterException, IOException{
	{
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey("pi7ymebgkpuhst3lC3aEQflyk")
			.setOAuthConsumerSecret("JRgVJm2A5ycsjPOhBvFZEU0WrG6gB2YuuA2FRIpGZinaGnIYRy")
			.setOAuthAccessToken("595124178-lWLnqxYxoh7Usm9Eis7Wh8wagePJ2ZnAjFclDPCy")
			.setOAuthAccessTokenSecret("VDT9l58yIMfHVG5ccBBzgBlBfNZhZ2mfhj4DHGICpVQV3")
			/* I added this because some tweets are longer than 140 characters due to retweets
			 * Sometimes the links are at the end of the tweet so we need to make sure that we get the whole tweet.
			 */
			.setTweetModeExtended(true); 
		
		StatusListener listener = new StatusListener() {
			int lines = 0;
			int filecount = 0;
			ArrayList<String> tweetlist = new ArrayList<String>();
            public void onStatus(Status status) {
            	String line = null;
            	if(status.getLang().contains("en")) {
                	lines++;
                	
	            	JSONArray users, hashtags, titles;
	            	JSONObject tweet = new JSONObject();
	            	boolean hasGeo = false;
            		
//	            	System.out.println("-------------------------------------Parsing the Tweet with Twitter4J------------------------------------");
//	            	System.out.println(status.getUser().getScreenName() + " : " + status.getText());
	            	String text = status.getText();
	            	
	            	//Fetches all User mentions within the tweet and prints them
	            	UserMentionEntity[] userList = status.getUserMentionEntities();
	            	users = new JSONArray();
	            	for(int i = 0; i < userList.length; i++) {
//	            		System.out.println("	User " + (i+1) + ": " + userList[i].getText());
	            		users.put(userList[i].getText());
	            	}
	            	
	            	
	            	//Fetches all URLs within the tweet and prints them
	            	URLEntity[] urlList = status.getURLEntities();
	            	for(int i = 0; i < urlList.length; i++) {
//	            		System.out.println("	URL " + (i+1) + ": " + urlList[i].getExpandedURL());
	            	}
	            	
	            	//Fetches all Hashtags within the tweet and prints them
	            	HashtagEntity[] hashtagList = status.getHashtagEntities();
	            	hashtags = new JSONArray();
	            	for(int i = 0; i < hashtagList.length; i++) {
//	            		System.out.println("	Hashtag " + (i+1) + ": " + hashtagList[i].getText());
	            		hashtags.put(hashtagList[i].getText());
	            	}
	            	
	            	//Fetches all media within a tweet (video, gif, picture etc.) and prints them (We won't need this unless we really want to add more features)
	            	MediaEntity[] mediaList = status.getMediaEntities();
	            	for(int i = 0; i < mediaList.length; i++) {
//	            		System.out.println("	Media " + (i+1) + " type : " + mediaList[i].getType() + " - " + mediaList[i].getExpandedURL());
	            	}
	            	
	            	if(status.getGeoLocation() != null) {
	            		hasGeo = true;
//	            		System.out.println(status.getPlace().getFullName() + " " + status.getPlace().getCountry());
	            	}
	            	
//	            	titles = new JSONArray();
//	            	for(int i = 0; i < urlList.length; i++) {
//		            	try 
//	            		{
//	            			URL url = new URL(urlList[i].getExpandedURL());
//	            			if(url.getHost() != null)
//	            			{
//	            				Document doc, doc2;
//	            				try	{
////	            					System.out.println("	-----------------Fetching URL titles with Jsoup----------------");
////	            					System.out.println("URL: " + urlList[i].getExpandedURL()); 
//	            					//Twitter uses a shorten URL i.e. http://t.co/rAndOmStUFf , so we need to ge the actual URL
//	            					doc = Jsoup.connect(urlList[i].getExpandedURL()).get();
//	            					String title = doc.title();//finds the title within the page
//	            					titles.put(title);
////	            					System.out.println("	title: " + title);
//	            				}
//	            				catch(Exception e)
//	        					{System.err.println("Unable to connect using Jsoup.");}
//	            			}
//	            		}
//	            		catch(MalformedURLException e) 
//	            			{System.err.println("Unable to fetch URL.");}
//	            	}
	            	
	            	try {
						tweet.put("name", status.getUser().getName());
						tweet.put("username", status.getUser().getScreenName());
						tweet.put("mentions", users);
						tweet.put("hashtags", hashtags);
//						tweet.put("url title", titles);
						tweet.put("text", text);
						if(hasGeo == true) {
							tweet.put("state", status.getPlace().getFullName());
							tweet.put("country", status.getPlace().getCountry());
						}
					} catch (JSONException e1) {
						System.err.println("JSON error");
					}
//	            	System.out.println(tweet.toString());
	            	tweetlist.add(tweet.toString() + "\n");
	            	if(lines%1000 == 0) {
	        			String filename = "E:\\CS172\\Tweets\\tweets" + filecount + ".json";
	                	try {
	                		File file = new File(filename);
							FileWriter fw = new FileWriter(file);
							for(int i = 0; i < tweetlist.size(); i++) {
								fw.write(tweetlist.get(i));
								fw.flush();
							}
							fw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
	                	tweetlist.clear();
	                	filecount++;
	            	}
	            	
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

