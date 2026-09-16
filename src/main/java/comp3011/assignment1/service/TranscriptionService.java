package comp3011.assignment1.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

// In order to understand where and how to implement the code, AI research was used to gain a greater understanding and to help de-bug parts of the code
// Handles communication with OpenAI service
@Service
public class TranscriptionService {

	// Store OpenAI transcription URL from application.properties
	private final String transcriptionUrl;
	
	// Store model name from application.properties
	private final String transcriptionModel;
	
	// Send http request to OpenAI
	private final RestClient restClient;
	
	private final GlobalStats globalStats;
	
	// Receives configurations and creates the http client
	public TranscriptionService(
			@Value("${openai.transcription.url}") String transcriptionUrl,
			@Value("${openai.transcription.model}") String transcriptionModel,
			GlobalStats globalStats) {
		
		this.transcriptionUrl = transcriptionUrl;
		this.transcriptionModel = transcriptionModel;
		this.globalStats = globalStats;
		this.restClient = RestClient.create();
	}
	
	// Checks if the API key exists in the system environment
	public boolean hasApiKey() {
		String apiKey = System.getenv("OPENAI_API_KEY");
		
		// If it is blank or doesn't exist, returns false
		return apiKey != null && !apiKey.isBlank();
	}
	
	// Token usage from OpenAI
	private record OpenAiUsage(
	        long input_tokens,
	        long output_tokens) {
	}
	
	// Response returned from OpenAI transcription api
	private record OpenAiTranscriptResponse(
	        String text,
	        OpenAiUsage usage) {
	}
	
	// Sends the audio recording to Open AI and returns the text
	public String transcribe(MultipartFile audio) throws IOException {
		String apiKey = System.getenv("OPENAI_API_KEY");
		
		// If no key is available, the code will stop before the API request
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalStateException("Error. OPENAI_API_KEY is not set");
		}
		
		// The uploaded audio becomes a named file and is now available
		// audio.getaBytes() reads the audio file into Java memory
		ByteArrayResource audioResource = new ByteArrayResource(audio.getBytes()) {
			@Override
			// Supplies a file name for the OpenAI API
			public String getFilename() {
				return "recording.webm";
			}
		};
		
		// The multipart form required by the transcription API is built
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		
		// Adds the audio under Open AI's field name 'file'
		bodyBuilder.part("file", audioResource)
				.contentType(audio.getContentType() != null
						? MediaType.parseMediaType(audio.getContentType())
						: MediaType.valueOf("audio/webm"));
		
		// Adds the transcription model
		bodyBuilder.part("model", transcriptionModel);
		
		// The request is sent with the server side API key and the JSON response is read
		OpenAiTranscriptResponse response = restClient.post()
				.uri(transcriptionUrl)
				
				// Authenticates the Java backend to OpenAI
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(bodyBuilder.build())
				.retrieve()
				
				// Maps the returned JSON's text field into java
				.body(OpenAiTranscriptResponse.class);
		
		// If the response contains no text, the problem is caught and can be handled
		if (response == null || response.text() == null) {
			throw new IllegalStateException("OpenAI transcription returned no text");
		}
		
		// If the response contains no token usage, the problem is handled
		if (response.usage() == null) {
			throw new IllegalStateException("OpenAI returned no token usage");
		}
		
		globalStats.addInputTokens(response.usage().input_tokens());
		globalStats.addOutputTokens(response.usage().output_tokens());
		
		// The transcription text is only then sent back to the controller
		return response.text();
		
	}
	
}
