package comp3011.assignment1.control;

import java.util.Map;

import comp3011.assignment1.service.GlobalStats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// provides the endpoint for retrieving global server statistics
@RestController
public class GlobalControl {
	
	// Stores the service used to retrieve the tokens
	public final GlobalStats globalStats;
	
	public GlobalControl(GlobalStats globalStats) {
		this.globalStats = globalStats;
	}
	
	// Returns the total number of input and output tokens used by the server
	@GetMapping("/api/v1/global/stats")
	public Map<String, Long> getGlobalStats() {
		return Map.of(
				"inputTokens", globalStats.getInputTokens(),
				"outputTokens", globalStats.getOutputTokens());
	}
}
