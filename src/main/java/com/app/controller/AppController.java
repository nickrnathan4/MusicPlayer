package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.app.DAL.SongDAL;
import com.app.models.IndexedSong;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class AppController {
	
	@Autowired
	private SongDAL songDAL;
	
    @RequestMapping("/")
    public String index() {
        
    	String mySongs = "";
    	
        List<IndexedSong> songs = songDAL.getSongs(); 
    	
        for(IndexedSong song : songs){
        	mySongs = mySongs + " | " + song.getSongTitle();
        }
    	
    	return mySongs;
    }

}