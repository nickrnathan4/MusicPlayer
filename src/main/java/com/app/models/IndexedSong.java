package com.app.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="IndexedSong")
public class IndexedSong {

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int songId; 
	
	private String songTitle;
    private String songArtist;
    private String songKey;
    
    
    @OneToMany(mappedBy="song")
    @JsonManagedReference
    private List<KeyPoint> keyPoints;

    // Getters
    public int getSongId() {
        return songId;
    }
    public String getSongTitle(){
    	return songTitle;
    }
    public String getSongArtist(){
        return songArtist;
    }
    public String getSongKey(){
        return songKey;
    }
    public List<KeyPoint> getKeyPoints(){
        return keyPoints;
    }

     // Setters
    public void setSongId(int i){
        this.songId = i;
    }
    public void setSongTitle(String s){
        this.songTitle = s;
    }
    public void setSongArtist(String a){
        this.songArtist = a;
    }
    public void setSongKey(String k){
        this.songKey = k;
    }
    public void setKeyPoints(List<KeyPoint> kp){
        this.keyPoints = kp;
    }
}