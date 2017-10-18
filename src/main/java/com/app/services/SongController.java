package com.app.services;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;


public class SongController implements Runnable { 

	boolean running, restart, paused;
	private int songId;
	private File file;
	private int byteChunkSize = 4096;
	private Thread t;
	private Object monitor;

	public SongController(Object monitor){

		file = null;
		paused = false;
        running = false;
        restart = false;
        songId = 0;
        this.monitor = monitor;
	}

	
	// ----------------------- PUBLIC METHODS ----------------------------- //

	public void play(){
        if(file != null && !running){
            try{          
            	t = new Thread(this);
            	t.start();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void pause(){
        if(running){       		
           paused = true;           
        }
    }

    public void resume(){
        if(paused){
            try{   
            	synchronized(monitor){
            		paused = false;      
              		monitor.notify(); 
              	}
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

	public void stop(){
        if(file != null){
            running = false;
            paused = false;
            songId = 0;
        }
    }

	public void run(){
        try {       
        	running = true;
        	do {
        		restart = false;        
	          	AudioInputStream in = AudioSystem.getAudioInputStream(file);
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
	        	rawplay(decodedFormat, din);
	    		in.close();
    		
    		} while(restart && running);

    		running = false;
        } 
        catch (Exception e) {
        	e.printStackTrace();
        } 
	} 

	public static void printAudioFormat(String mp3){
		
		try {

			AudioInputStream in = AudioSystem.getAudioInputStream(new File(mp3));
			AudioFormat songFormat = in.getFormat();

			System.out.println();

			System.out.print("Summary: ");
			System.out.println(songFormat.toString());

			System.out.print("Encoding: ");
			System.out.println(songFormat.getEncoding());

			System.out.print("Channels: ");
			System.out.println(songFormat.getChannels());

			System.out.print("Frame Rate: ");
			System.out.println(songFormat.getFrameRate());

			System.out.print("Frame Size (bytes): ");
			System.out.println(songFormat.getFrameSize());

			System.out.print("Sample Rate: ");
			System.out.println(songFormat.getSampleRate());

			System.out.print("Sample Size (bytes): ");
			System.out.println(songFormat.getSampleSizeInBits()/8);

			System.out.print("Big Endian: ");
			System.out.println(songFormat.isBigEndian());

			System.out.println();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public boolean isPaused(){
		return paused;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public void setRunningSongId(int songId){
		this.songId = songId;
	}
	
	public int getRunningSongId(){
		return songId;
	}

	// ----------------------- PRIVATE METHODS ----------------------------- //


	public boolean loadFile(File fin){
        file = fin;
        if(file.exists() && file.getName().toLowerCase().endsWith(".mp3") && !running){
            return true;
        }
        else{
            file = null;
            return false;
        }
    }

	private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException
	{
		try{

		  byte[] data = new byte[byteChunkSize];
		  SourceDataLine line = getLine(targetFormat); 
		  if (line != null)
		  {
		    // Start
		    line.start();
		    int nBytesRead = 0, nBytesWritten = 0;
		    while (nBytesRead != -1 && running)
		    {
		        nBytesRead = din.read(data, 0, data.length);
		        if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);		   

		        while(paused && running){
                        synchronized(monitor){
                        	monitor.wait();                    	 
                        }
                    }
		    }
		    // Stop
		    line.drain();
		    line.stop();
		    line.close();
		    din.close();
		  } 
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(LineUnavailableException lue){
			lue.printStackTrace();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}

	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
	  SourceDataLine res = null;
	  DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	  res = (SourceDataLine) AudioSystem.getLine(info);
	  res.open(audioFormat);
	  return res;
	}

}