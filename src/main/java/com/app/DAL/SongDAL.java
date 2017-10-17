package com.app.DAL;

import com.app.models.IndexedSong;
import com.app.models.KeyPoint;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.nio.ByteBuffer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
 
@Repository
@Transactional
public class SongDAL {  

	@PersistenceContext	
	private EntityManager entityManager;	


	// ----------------------- PUBLIC METHODS ----------------------------- //

	public void indexSong(String artist, String mp3) {
		 
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

	@SuppressWarnings("unchecked")
	public IndexedSong getSong(int songId) {
		
		IndexedSong song = null;
	    
		try{
	    	String query = "FROM IndexedSong AS Song WHERE Song.songId = ?";
	    	List<IndexedSong> songs = entityManager.createQuery(query).setParameter(1, songId).getResultList();
	    	if(!songs.isEmpty()){
	    		song = songs.get(0);
	    	}	    	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    return song;
	}
	
	@SuppressWarnings("unchecked")
	public IndexedSong getSongByTitle(String title) {
		
		IndexedSong song = null;
	    
		try{
	    	String query = "FROM IndexedSong AS Song WHERE Song.songTitle = ?";
	    	List<IndexedSong> songs = entityManager.createQuery(query).setParameter(1, title).getResultList();
	    	if(!songs.isEmpty()){
	    		song = songs.get(0);
	    	}	    	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    return song;
	}

	@SuppressWarnings("unchecked")
	public List<IndexedSong> getSongs() {
		
	    List<IndexedSong> songs = null;
	    
	    try{
	    	String query = "SELECT Songs FROM IndexedSong AS Songs ORDER BY Songs.songArtist";
	    	songs = entityManager.createQuery(query).getResultList();
	    	// Hibernate.initialize(songs); // eagerly evaluate	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    return songs;
	}

	public List<KeyPoint> getKeyPoints(int songId) {

	    List<KeyPoint> kps = null;
	    
	    try{
        	IndexedSong song = getSong(songId);
    		kps = song.getKeyPoints();
    		//Hibernate.initialize(kps); // eagerly evaluate	
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    return kps;
	}


	// ---------------- PRIVATE METHODS ---------------------- // 

	private IndexedSong addNewSong(String title, String artist, String path) {
	    
	    try{
	    	
	    	IndexedSong song = getSongByTitle(title);
	    	
	    	if(song != null) {
		    	
		    	IndexedSong s = new IndexedSong();   
			    
			    s.setSongTitle(title);
			    s.setSongArtist(artist);
			    s.setSongPath(path);			  
			    s.setKeyPoints(new ArrayList<KeyPoint>());			    
			    entityManager.persist(s);
			   	return s;
	    	}
	    	else {
			   	System.out.println(title.concat(" already exists."));  
	    	}
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }  
	    return null;
	}

	private void addKeyPoints(IndexedSong song, List<Integer> keyPoints) {

	    int size = keyPoints.size();
	    int sampleSize = size / 15000;
	    int insertCount = 0;
	    try{
	    	for(int i=0;i<size;++i){
		    		if( i % sampleSize == 0) {

				    	KeyPoint kp = new KeyPoint();   
					   	kp.setTime(insertCount);
					   	kp.setValue(keyPoints.get(i));
					   	kp.setSong(song);
					    entityManager.persist(kp); 
			    		++insertCount;
	    		}
	    	}
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
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