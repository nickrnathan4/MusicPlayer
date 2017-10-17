package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.DAL.SongDAL;
import com.app.models.IndexedSong;
import com.app.models.KeyPoint;
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
            	
        List<IndexedSong> songs = songDAL.getSongs();           
        model.addAttribute("songs", songs); 
    	return "index";
    }
    
    @PostMapping("/play")
    public String play(@RequestParam("songId") String songId, Model model) {    	
		try {
			if(controller.isPaused()){
				controller.resume();
			}
			else {
				String fname = songDAL.getSong(Integer.parseInt(songId)).getSongPath();
				if(!fname.isEmpty()) {
					
					// Play Song
					Resource audioFile = new ClassPathResource("audio/" + fname);
					if(controller.loadFile(audioFile.getFile()) ){
						controller.play();
					}
					
					// Display Song
					List<KeyPoint> kps = songDAL.getKeyPoints(Integer.parseInt(songId));					
					System.out.println(kps.size());
					model.addAttribute("keypoints", kps);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}		
		
		return "index";
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
}