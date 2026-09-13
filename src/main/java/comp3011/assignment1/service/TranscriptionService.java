package comp3011.assignment1.service;

import org.springframework.stereotype.Service;

// Handles communication with OpenAI service
@Service
public class TranscriptionService {

	// Checks if the API key exists in the system environment
	public boolean hasApiKey() {
		String apiKey = System.getenv("OPENAI_API_KEY");
		
		// If it is blank or doesn't exist, returns false
		return apiKey != null && !apiKey.isBlank();
	}
}
