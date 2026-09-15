package comp3011.assignment1.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

// Stores token counters for however long the server process lasts
@Service
public class GlobalStats {
	
	// AtomicLong allows the counters to be updated while multiple requests are being processed
	private final AtomicLong inputTokens = new AtomicLong();
	private final AtomicLong outputTokens = new AtomicLong();
	
	// Adds the number of input tokens used by a request to the total count
	public void addInputTokens(long tokens) {
		inputTokens.addAndGet(tokens);
	}
	
	// Adds the number of output tokens created by a request to the total count
	public void addOutputTokens(long tokens) {
		outputTokens.addAndGet(tokens);
	}
	
	// Returns the total number of input tokens used since the server started
	public long getInputTokens() {
		return inputTokens.get();
	}
	
	// Returns the total number of output tokens generated since the server started
	public long getOutputTokens() {
		return outputTokens.get();
	}
}
