package app;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.hibernate.Session;  
import org.hibernate.Query;  
import org.hibernate.SessionFactory;  
import org.hibernate.Transaction;  
import org.hibernate.cfg.Configuration;
import org.hibernate.Hibernate;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
  
public class SongDAL {  

	private static Configuration cfg;
	private static SessionFactory factory;

	public SongDAL() {  
		try {
		    //creating configuration object  
		    cfg = new Configuration();  
		    cfg.configure("hibernate.cfg.xml");//populates the data of the configuration file  	       
		    
		    // Update config for bulk operations
		    cfg.setProperty("hibernate.jdbc.batch_size ", "30" );
			cfg.setProperty("hibernate.cache.use_second_level_cache", "false" );	

			//creating seession factory objects
		    factory = cfg.buildSessionFactory();

	    } 
	    catch (Exception e) {
	        System.err.println("Initial SessionFactory creation failed." + e);
		}	
	}		

	// ----------------------- PUBLIC METHODS ----------------------------- //

	public static void indexSong(String artist, String mp3) {
		 
		 try{

		 	File f = new File(mp3);			
			if(f.exists()) { 

	           	String title = mp3.split("\\.")[0];
	           	IndexedSong newSong = addNewSong(title, artist, mp3);

	           	if(newSong != null){

					AudioInputStream in = AudioSystem.getAudioInputStream(f);
					AudioInputStream din = null;
					AudioFormat baseFormat = in.getFormat();
					AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
					                                            baseFormat.getSampleRate(),
					                                            16,
					                                            baseFormat.getChannels(),
					                                            baseFormat.getChannels() * 2,
					                                            baseFormat.getSampleRate(),
					                                            true);
					din = AudioSystem.getAudioInputStream(decodedFormat, in);

					List<Integer> songArray = new ArrayList<Integer>();

					buildSongArray(din,songArray);		
					addKeyPoints(newSong,songArray);

					in.close();
				}
			}
			else{
				System.out.println("File path could not be found.");
			}		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static IndexedSong getSong(String title) {
		Session session = factory.openSession();   
	    Transaction tx = null;
		IndexedSong song = null;
	    try{
			tx = session.beginTransaction(); 
	    	Query q = session.createQuery("FROM IndexedSong WHERE songTitle = :t").setParameter("t",title);
	    	List<IndexedSong> songs = q.list();
	    	for (Iterator<IndexedSong> itr = songs.iterator(); itr.hasNext();){
            	song = itr.next();
	    	}
	    	tx.commit();	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    session.close();  
	    return song;

	}

	public static List<IndexedSong> getSongs() {
		Session session = factory.openSession();   
	    Transaction tx = null;
	    List<IndexedSong> songs = null;
	    try{
			tx = session.beginTransaction(); 
	    	Query q = session.createQuery("FROM IndexedSong");
	    	songs = q.list();
	    	Hibernate.initialize(songs);
	    	tx.commit();	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    session.close();  
	    return songs;

	}

	public static List<KeyPoint> getKeyPoints(String title) {
	
		Session session = factory.openSession();   
	    Transaction tx = null;
	    List<KeyPoint> kp = null;
	    
	    try{
			tx = session.beginTransaction(); 
	    	Query q = session.createQuery("FROM IndexedSong WHERE songTitle = :t").setParameter("t",title);
	    	List<IndexedSong> songs = q.list();
	    	for (Iterator<IndexedSong> itr = songs.iterator(); itr.hasNext();){
            	IndexedSong song = itr.next();
	    		kp = song.getKeyPoints();
	    		Hibernate.initialize(kp); // eagerly evaluate
	    	}
	    	tx.commit();	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    session.close();  
	    return kp;
	}

	public void disconnect() throws Exception {
		factory.close();
	}



	// ---------------- PRIVATE METHODS ---------------------- // 

	private static IndexedSong addNewSong(String title, String artist, String path) {
	
		Session session = factory.openSession();   
	    Transaction tx = null;
	    
	    try{
	    	
	    	tx = session.beginTransaction();
	    	Query q = session.createQuery("FROM IndexedSong WHERE songTitle = :t").setParameter("t",title);
	    	List<IndexedSong> l = q.list(); 
	    	
	    	if(l.isEmpty()) {
		    	
		    	IndexedSong s = new IndexedSong();   
			    
			    s.setSongTitle(title);
			    s.setSongArtist(artist);
			    s.setSongPath(path);			  
			    s.setKeyPoints(new ArrayList<KeyPoint>());
			    
			    session.save(s);  
			    tx.commit();
			   	System.out.println("Successfully saved ".concat(title));  
			   	return s;
	    	}
	    	else {
			   	System.out.println(title.concat(" already exists."));  
	    	}

	    } 
	    catch(Exception e){
	    	if (tx!=null) tx.rollback();
	    	e.printStackTrace();
	    	return null;
	    }

	    session.close();  
	    return null;
	}


	private static void addKeyPoints(IndexedSong song, List<Integer> keyPoints) {
	
		Session session = factory.openSession();   
	    Transaction tx = null; 
	    int size = keyPoints.size();
	    int sampleSize = size / 15000;
	    int insertCount = 0;

	    try{
	    	tx = session.beginTransaction();
	    	for(int i=0;i<size;++i){

		    		if( i % sampleSize == 0) {

				    	KeyPoint kp = new KeyPoint();   
					   	kp.setTime(insertCount);
					   	kp.setValue(keyPoints.get(i));
					   	kp.setSong(song);
					    session.save(kp); 
					    
				        // flush a batch of inserts and release memory:
					    if ( insertCount % 30 == 0 ) { 
					        session.flush();
					        session.clear();        
			    		}
			    		++insertCount;
	    		}
	    	}

		    tx.commit();
		   	System.out.println("Key points successfully saved.");  
	    } 
	    catch(Exception e){
	    	if (tx!=null) tx.rollback();
	    	e.printStackTrace();
	    }
	    
	    session.close();  
	}


	private static short castBytetoShort(byte a, byte b) {
			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.put(a);
			bb.put(b);
			return bb.getShort(0);
	}


	private static void buildSongArray(AudioInputStream s, List<Integer> out){
		
		byte[] buff = new byte[4];
		int bytesRead = 0;
		int lchannel;
		int rchannel;

		try{
			while (bytesRead != -1){
				bytesRead = s.read(buff, 0, buff.length);
				lchannel = (int)castBytetoShort(buff[0],buff[1]);
				rchannel = (int)castBytetoShort(buff[2],buff[3]);
				out.add((lchannel+rchannel)/2);
			}

			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}  