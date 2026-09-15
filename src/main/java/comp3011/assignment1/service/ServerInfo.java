package comp3011.assignment1.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

// Stores the information that belongs to the current server process
@Service
public class ServerInfo {

	// Captures the current time when the service is created when the code begins running
	private final Instant serverStart = Instant.now();
	
	// Returns the time when the server process begins
	public Instant getServerStart() {
		return serverStart;
	}
	
	// Returns the current timestamp 
	public Instant getNow() {
		return Instant.now();
	}
	
	// Returns how long the server has been running for 
	public double getUptimeSeconds() {
		Instant now = getNow();
		
		return Duration.between(serverStart, now).toSeconds();
	}
	
}
