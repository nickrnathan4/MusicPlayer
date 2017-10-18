package com.app.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name="KeyPoint")
public class KeyPoint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	private int value;
 	private int time;
 	
 	@ManyToOne
    @JoinColumn(name="songId", nullable=false)
 	@JsonBackReference
    private IndexedSong song;

    // Getters
    public int getId() {
        return id;
    }
    public int getTime() {
        return time;
    }
    public int getValue(){
    	return value;
    }
    public IndexedSong getSong(){
        return song;
    }

    // Setters
    public void setId(int i){
        this.id = i;
    }
    public void setTime(int t){
        this.time = t;
    }
    public void setValue(int v){
        this.value = v;
    }
    public void setSong(IndexedSong s){
        this.song = s;
    }
}