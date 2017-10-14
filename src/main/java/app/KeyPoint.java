package app;

class KeyPoint {

    private int id;
	private int value;
 	private int time;
    private IndexedSong song;

    // Default Constructor
    public KeyPoint() {}

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