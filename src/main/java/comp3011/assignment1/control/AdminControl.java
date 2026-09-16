package comp3011.assignment1.control;

import java.time.Instant;
import java.util.Map;

import comp3011.assignment1.service.ServerInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// For parts of this class, generative AI was used to de-bug parts where I could not find the problem, and to speed up the process.
// Provides the endpoints needed for the assignment
@RestController
public class AdminControl {
	
	private final ServerInfo serverInfo;
	private final ConfigurableApplicationContext applicationContext;
	
	public AdminControl(
			ServerInfo serverInfo,
			ConfigurableApplicationContext applicationContext) {
		
		this.serverInfo = serverInfo;
		this.applicationContext = applicationContext;
	}
	
	// Returns the start time, current time, and elapsed time
	@GetMapping("/api/v1/admin/uptime")
	public Map<String, Object> getUpTime() {
		
		// Gets the time in UTC from ServerInfo, so all times are generated consistently
		Instant utcNow = serverInfo.getNow();
		
		return Map.of(
				"utcServerStart", serverInfo.getServerStart(),
				"utcNow", utcNow,
				"serverUptimeSeconds", serverInfo.getUptimeSeconds());
	}
	
	// Accepts the shutdown request, then closes the application afterwards
	@PostMapping("/api/v1/admin/shutdown")
	public ResponseEntity<Map<String, String>> shutdown() {
		
		// The shutdown is performed, so the request can return a response before the application context closes
		Thread shutdownThread = new Thread(() -> {
			try {
				
				// The shutdown is delayed so the HTTP request can be sent before the application closes
				Thread.sleep(100);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
			}
			
			// Closes the spring application context
			int exitCode = SpringApplication.exit(applicationContext, () -> 0);
			
			// Terminates the JVM
			System.exit(exitCode);
		});
		
		shutdownThread.start();
		
		// As the shutdown is performed separately, Accepted indicates the request was received and shutdown has started 
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(Map.of("message", "Graceful shutdown requested"));
	}
}