package org.twitter.crawler;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
//import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import java.io.*;
import org.jsoup.*;
//import twitter4j.*;
import org.json.simple.JSONObject;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.lang.Iterable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class index2 
{
	
	
	public static void main(String[] args) throws IOException, org.apache.lucene.queryparser.classic.ParseException 
	{
		Analyzer analyzer = new StandardAnalyzer();

        // Store the index in memory:
//        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        Directory directory = FSDirectory.open(Paths.get("/home/jiqingjerry/Documents/indexTweets"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer).setOpenMode(OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        int numFiles = 2;
        for(int i = 0; i < numFiles; i++)
        {
			JSONParser parser = new JSONParser();
			try
			{
				String filePath = "/home/jiqingjerry/Documents/tweets/num"+i+".json";
				System.out.println("Reading " + filePath);
				JSONArray a = (JSONArray) parser.parse(new FileReader(filePath));
				for(Object o : a)
				{
					JSONObject tweet = (JSONObject) o;
					Document doc = new Document();
					String name = (String) tweet.get("name");
					String username = (String) tweet.get("username");
					String text = (String) tweet.get("text");
					String date = (String) tweet.get("date");
		            doc.add(new StringField("Name", name, Field.Store.YES));
		            doc.add(new StringField("UserName", username, Field.Store.YES));
		            doc.add(new TextField("Text", text, Field.Store.YES));
		            doc.add(new StringField("Date", date, Field.Store.YES));
		            //Add more here
		            indexWriter.addDocument(doc);
	
				}
				
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
        }
        indexWriter.close();
		
		
		// Now search the index:
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        String[] fields = {"Name", "UserName", "Text"}; //Add the fields here
        Map<String, Float> boosts = new HashMap<>();
        boosts.put(fields[0], 1.0f);
        boosts.put(fields[1], 1.0f);
        boosts.put(fields[2], .75f);
        //adjust these 
       	MultiFieldQueryParser parser2 = new MultiFieldQueryParser(fields, analyzer, boosts);
       	Query query = parser2.parse("i");
       	int topHitCount = 100;
       	
//       	Sort sort = new Sort(SortField.FIELD_SCORE, new SortField("Date", SortField.Type.STRING));
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;
		
        
        // Iterate through the results:
        for (int rank = 0; rank < hits.length; ++rank) {
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            System.out.println((rank + 1) + " (score:" + hits[rank].score + ") " + hitDoc.get("Date") + " --> " +
                               hitDoc.get("Name") + " - " + hitDoc.get("Text"));
            // System.out.println(indexSearcher.explain(query, hits[rank].doc));
        }
        indexReader.close();
        directory.close();
	}
	
}
