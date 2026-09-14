package comp3011.assignment1.control;

import comp3011.assignment1.service.TranscriptionService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


// Allows this class to receive requests and return data
@RestController
public class AudioControl {
	
	private final TranscriptionService transcriptionService;
	
	// The transcription service is put into the audio controller
	public AudioControl(TranscriptionService transcriptionService) {
		this.transcriptionService = transcriptionService;
	}
	
	
	// Receives a POST request that has a multi-part audio file
	@PostMapping(
		
		// Browser will send the recording to this location
		value = "/api/audio",
		
		// The end-point expects a form containing a file now
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		
	public ResponseEntity<String> receiveAudio(
			@RequestParam("audio") MultipartFile audio) {
			
			// The request will be rejected if no audio was uploaded
			if (audio.isEmpty()) {
				return ResponseEntity.badRequest()
						.body("No audio recording was received.");
			}
			
			try {
				
				// The uploaded recording is sent to the OpenAI transcription service
				String transcription = transcriptionService.transcribe(audio);
				
				// The recognised words from the recording are then sent to the browser
				return ResponseEntity.ok(transcription);
				
			} catch (IllegalStateException exception) {
				
				// When the OpenAI api key is not available, this will show
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body(exception.getMessage());
				
			} catch (Exception exception) {
				
				// Checks and handles errors from OpenAI transcription request
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
						.body("The transcription service could not process the recording.");
			}
		}

}

