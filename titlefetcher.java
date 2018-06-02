package twitter;

import twitter4j.*;
import java.io.*;
import twitter4j.conf.ConfigurationBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.net.MalformedURLException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.concurrent.TimeUnit;



public class titlefetcher implements Runnable 
{
	
	private int seed;
	private int delta;
	public titlefetcher(int seed, int delta)
	{
		this.seed = seed;
		this.delta = delta;
	}
	

	@SuppressWarnings("unchecked")
	public void run() 
	{
		int filecount = seed;
		while(true)
		{
			String filePath = "tweets/conn/num" + filecount + ".json";
			while(true)
			{
				File check = new File(filePath);
				if (check.exists())
				{
//					System.out.println("Leaving if statement with " + filePath);
					break;
				}
				else
					try {
						TimeUnit.SECONDS.sleep(2);
//						TimeUnit.MINUTES.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			JSONParser parser = new JSONParser();
			try
			{
				
				System.out.println("    Thread(" +seed+ ") Reading " + filePath);
				ArrayList<String> tweetlist = new ArrayList<String>();
				org.json.simple.JSONArray a = (org.json.simple.JSONArray) parser.parse(new FileReader(filePath));
				for(Object o : a)
				{
					JSONObject tweet = (JSONObject) o;
					String urlList = (String) tweet.get("url title");
					ArrayList<String> titles = new ArrayList<String>();
					if (urlList.length() > 2)
					{
						String array[] = urlList.split("\"");
						for(int i = 0; i < array.length; i++)
						{
							if (!array[i].equals( "[") && !array[i].equals( "]") && !array[i].equals(","))
								try 
								{
									URL url = new URL (array[i]);
									if (url.getHost() != null)
									{
										Document doc;
										try
										{
											doc = Jsoup.connect(array[i]).get();
											titles.add(doc.title());
											
										}
										catch (Exception e) 
										{/*System.err.println(" Failed to Fetch title");*/}
									}
								}
								catch (MalformedURLException e)
								{}
						}
						
					}
					tweet.put("titles", titles);
					tweetlist.add(tweet.toString());
				}
				try {
					String filename = "tweets/conn/numZ" + filecount + ".json";
					filecount+=delta;
					System.out.println("    Thread("+seed + ") Writing to " + filename);
	        		File file = new File(filename);
					FileWriter fw = new FileWriter(file);
					fw.write("[");
					fw.flush();
					for(int i = 0; i < tweetlist.size(); i++) {
						fw.write(tweetlist.get(i));
						if (i != tweetlist.size()-1)
							fw.write(",");
						fw.flush();
					}
					fw.write("]");
					fw.flush();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
    	
	}
	
	public static void main(String[] args) throws TwitterException, IOException
	{
		int processors = Runtime.getRuntime().availableProcessors();
	    System.out.println("CPU cores: " + processors);
	    for( int i = 0; i < processors; i ++)
	    	(new Thread(new titlefetcher(i,processors))).start();
				
	}
}
