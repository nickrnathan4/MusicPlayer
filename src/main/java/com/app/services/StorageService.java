package com.app.services;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.app.DAL.SongDAL;
import com.amazonaws.services.s3.model.PutObjectResult;

@Service
public class StorageService {
	
	private Logger logger = LoggerFactory.getLogger(StorageService.class);

	
	@Autowired
	private SongDAL songDAL;
	
	@Autowired
	private AmazonS3 s3client;
 
	@Value("${jsa.s3.bucket}")
	private String bucketName;
	
	@Value("${jsa.s3.tmpUploadPath}")
	private String tmpPath;
 
	public S3Object fetchFile(String key) {

		try {			
			S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, key));
			return s3object;
            
        } catch (AmazonServiceException ase) {
        	logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        }
		
		return null;
	}
 
	public void storeFile(File mp3, String originalFileName, String artistName) {
		
		try {
			
			UUID uuid = UUID.randomUUID();
	        String key = uuid.toString();
	        PutObjectResult result = s3client.putObject(new PutObjectRequest(bucketName, key, mp3));
	        if(result != null){
	        	String songTitle = originalFileName.substring(0,originalFileName.lastIndexOf('.'));
	        	songDAL.indexSong(mp3, songTitle, artistName, key);
	        }
	        
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        }
	}
	
	public File loadFile(MultipartFile file){
		File mp3 = convert(file);
		if(mp3.exists() && mp3.getName().toLowerCase().endsWith(".mp3")){
            return mp3;
        }
		return null;
	}
	
	// --- PRIVATE METHODS ---- //
	
	private File convert(MultipartFile file)
	{    
		File convFile = null;
		try {
			convFile = new File(tmpPath + file.getOriginalFilename());
		    FileOutputStream fos;
		    fos = new FileOutputStream(convFile);
		    fos.write(file.getBytes());
		    fos.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	    return convFile;
	}
	
 
}