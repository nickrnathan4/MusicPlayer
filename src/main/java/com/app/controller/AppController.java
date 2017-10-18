package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.DAL.SongDAL;
import com.app.models.IndexedSong;
import com.app.services.SongController;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppController {
	
	@Autowired
	private SongDAL songDAL;

	public Object monitor = new Object();
	public SongController controller = new SongController(monitor);
	
    @RequestMapping("/")
    public String index(Model model) {
    	
    	if(controller.isRunning()){
			controller.stop();
		}
    	
        List<IndexedSong> songs = songDAL.getSongs();           
        model.addAttribute("songs", songs); 
    	return "index";
    }
    
    @PostMapping("/play")
    public String play(@RequestParam("songId") String songId) {    	
		try {
			int id = songId.trim().isEmpty() ? 0 : Integer.parseInt(songId);
			
			if(controller.isPaused() && id == controller.getRunningSongId()){
				controller.resume();
			}
			else {
				controller.stop();				
				String fname = songDAL.getSong(id).getSongPath();
				if(!fname.isEmpty()) {					
					// Play Song
					Resource audioFile = new ClassPathResource("audio/" + fname);
					if(controller.loadFile(audioFile.getFile()) ){
						controller.setRunningSongId(id);
						controller.play();
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}		
		
		return "index :: main-content";
    }
    
    @RequestMapping("/pause")
    public String pause() {
    	if(!controller.isPaused()){
    		controller.pause();
    	}
    	return "index :: songlist";
    }
  
    @RequestMapping("/stop")
    public String stop() {
    	if(controller.isRunning()){
			controller.stop();
		}
    	return "index :: songlist";
    }
    
    @PostMapping("/song")
    public @ResponseBody IndexedSong song(@RequestParam(value="songId",required=false) String songId ) {
    	IndexedSong song = null;
    	int id = 0;
    	try {
    		id = (songId == null) ? controller.getRunningSongId() : Integer.parseInt(songId);
    		if(id != 0) {
	    		song = songDAL.getSong(id);
	    	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return song;
    }

}