package com.applitools.quickstarts;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Unit test for simple App.
 */
public class AppTest {

	public static void main(String[] args) {
		// Create a new chrome web driver
		WebDriver webDriver = new ChromeDriver(new ChromeOptions().setHeadless(getCI()));

		// Create a runner with concurrency of 5
		VisualGridRunner runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));

		// Create Eyes object with the runner, meaning it'll be a Visual Grid eyes.
		Eyes eyes = new Eyes(runner);

		setUp(eyes);

		try {
			// ⭐️ Note to see visual bugs, run the test using the above URL for the 1st run.
			// but then change the above URL to https://demo.applitools.com/index_v2.html
			// (for the 2nd run)
			ultraFastTest(webDriver, eyes);

		} finally {
			tearDown(webDriver, runner);
		}

	}

	public static boolean getCI() {
		String env = System.getenv("CI");
		return Boolean.parseBoolean(env);
	}

	public static void setUp(Eyes eyes) {

		// Initialize eyes Configuration
		Configuration config = eyes.getConfiguration();

		// You can get your api key from the Applitools dashboard
		config.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

		// create a new batch info instance and set it to the configuration
		BatchInfo myBatch = new BatchInfo("Browser combo batch");
	        myBatch.setId(process.env.APPLITOOLS_BATCH_ID);
	        myBatch.addProperty("RunID", "GithubActions" + url);
	//        myBatch.addProperty("runner", "VG");
        	sconf.setBatch(myBatch);

		// Add browsers with different viewports
		config.addBrowser(800, 600, BrowserType.CHROME);
		config.addBrowser(700, 500, BrowserType.FIREFOX);
		config.addBrowser(1600, 1200, BrowserType.IE_11);
		config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM);
		config.addBrowser(800, 600, BrowserType.SAFARI);

		// Add mobile emulation devices in Portrait mode
		config.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
		config.addDeviceEmulation(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT);

		// Set the configuration object to eyes
		eyes.setConfiguration(config);

	}

	public static void ultraFastTest(WebDriver webDriver, Eyes eyes) {

		try {

			// Navigate to the url we want to test
			webDriver.get("https://demo.applitools.com");

			// Call Open on eyes to initialize a test session
			eyes.open(webDriver, "Demo App - Selenium for Java - Ultrafast", "Smoke Test - Selenium for Java - Ultrafast", new RectangleSize(800, 600));

			// check the login page with fluent api, see more info here
			// https://applitools.com/docs/topics/sdk/the-eyes-sdk-check-fluent-api.html
			eyes.check(Target.window().fully().withName("Login page"));

			webDriver.findElement(By.id("log-in")).click();

			// Check the app page
			eyes.check(Target.window().fully().withName("App page"));

			// Call Close on eyes to let the server know it should display the results
			eyes.closeAsync();

		} finally  {
			eyes.abortAsync();
		}

	}

	private static void tearDown(WebDriver webDriver, VisualGridRunner runner) {
		// Close the browser
		webDriver.quit();

		// find visual differences
		TestResultsSummary allTestResults = runner.getAllTestResults(true);
		System.out.println(allTestResults);
	}

}
