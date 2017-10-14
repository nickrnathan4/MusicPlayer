package app;

import java.util.List;

class IndexedSong {

	private String songTitle;
    private String songArtist;
    private String songPath;
 	private int songId; 
    private List<KeyPoint> keyPoints;

    // Default Constructor
    public IndexedSong() {}

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
    public String getSongPath(){
        return songPath;
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
    public void setSongPath(String p){
        this.songPath = p;
    }
    public void setKeyPoints(List<KeyPoint> kp){
        this.keyPoints = kp;
    }
}