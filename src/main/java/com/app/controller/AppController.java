package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.DAL.SongDAL;
import com.app.models.IndexedSong;
import com.app.services.SongController;
import com.app.services.StorageService;

@Controller
public class AppController {
	
	@Autowired
	private SongDAL songDAL;
	
	@Autowired
	public StorageService store;

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
				String key = songDAL.getSong(id).getSongKey();
				if(!key.isEmpty()) {						
					
					// Play Song					
					controller.loadFile(store.fetchFile(key));
					controller.setRunningSongId(id);
					controller.play();
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
    
    @RequestMapping("/upload")
    public String addSong() throws IOException {
    	return "upload";
    }
    
    @PostMapping("/uploadSong")
    public String uploadSong(@RequestParam("file") MultipartFile file, 
    							@RequestParam("artistName") String artistName,
            RedirectAttributes redirectAttributes) {
    	
    	File mp3 = store.loadFile(file);
		store.storeFile(mp3, file.getOriginalFilename(), artistName);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/upload";
    }

    @PostMapping("/mp3") 
    public @ResponseBody byte[] mp3(@RequestParam("songId") String songId) throws IOException {
    	 try {
	    	 int id = songId.trim().isEmpty() ? 0 : Integer.parseInt(songId);
	    	 IndexedSong song = songDAL.getSong(id);
	    	 String key = song.getSongKey();
	    	 if(key != null){ 
	    		 InputStream in = store.fetchFile(key).getObjectContent();
	    		 return IOUtils.toByteArray(in);
	    	 }
    	 }
    	 catch(Exception e){
    		 e.printStackTrace();
    	 }
    	return null;
    }

}