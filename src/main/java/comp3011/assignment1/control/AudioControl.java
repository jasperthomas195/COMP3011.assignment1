package comp3011.assignment1.control;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// Allows this class to receive requests and return data
@RestController
public class AudioControl {
	
	// Receives a POST request that has a multi-part audio file
	@PostMapping(
		
		// Browser will send the recording to this location
		value = "/api/audio",
		
		// The end-point expects a form containing a file now
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public Map<String, Object> receiveAudio(
			
			// Receives the audio file with the name "audio"
			@RequestParam("audio") MultipartFile audio) {
		
		
		// Information is sent about the recording back to the browser
		return Map.of(
				"message", "Server has received recording.",
				"fileName", audio.getOriginalFilename(),
				"size", audio.getSize(),
				"contentType", audio.getContentType()
			);		
	}		
}

