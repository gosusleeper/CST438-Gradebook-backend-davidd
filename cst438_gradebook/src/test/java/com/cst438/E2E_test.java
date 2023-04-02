package com.cst438;


import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;


public class E2E_test {
	public static final String CHROME_DRIVER_FILE_LOCATION 
                          = "/usr/bin/safaridriver";
	public static final String URL = "http://localhost:3000/problem";
	public static final String ALIAS_NAME = "test";
	public static final String ALIAS_DATE = "2023-01-01";
	public static final String ALIAS_COURSEID = "123456";

	public static final int SLEEP_DURATION = 1000; // 1 second.


	@Test
	public void playGame() throws Exception {


		// set the driver location and start driver
		//@formatter:off
		//
		// browser	property name 				Java Driver Class
		// -------  ------------------------    ----------------------
		// Edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		// Chrome   webdriver.chrome.driver     ChromeDriver
		// Safari   webdriver.safari.driver		SafariDriver
		//
		//@formatter:on


		//TODO update the property name for your browser 
		System.setProperty("webdriver.safari.driver",
                     CHROME_DRIVER_FILE_LOCATION);
		//TODO update the class ChromeDriver()  for your browser
		WebDriver driver = new SafariDriver();
		
		try {
			WebElement we;
			
			driver.get(URL);
			// must have a short wait to allow time for the page to download 
			Thread.sleep(SLEEP_DURATION);


			// get the 2 multiply factors
			//String factora = driver.findElement(By.id("factora")).getText();
			//String factorb = driver.findElement(By.id("factorb")).getText();


			// enter the answer.  
			// find the input tag with name="attempt"
			//String attempt = Integer.toString(
            //              Integer.parseInt(factora) * Integer.parseInt(factorb));
			//we = driver.findElement(By.name("attempt"));
			//we.sendKeys(attempt);
			
			// enter an alias name
			we = driver.findElement(By.name("names"));
			we.sendKeys(ALIAS_NAME);
			
			we = driver.findElement(By.name("date"));
			we.sendKeys(ALIAS_DATE);
			
			we = driver.findElement(By.name("course_id"));
			we.sendKeys(ALIAS_COURSEID);
			
			// find and click the submit button
			we = driver.findElement(By.id("submit"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// verify the correct message
			we = driver.findElement(By.id("message"));
			String message = we.getText();
			assertEquals("working", message);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			
		} finally {
			driver.quit();
		}
	}
		
		@Test
		public void playBadGame() throws Exception {


			System.setProperty("webdriver.chrome.driver",
                                         CHROME_DRIVER_FILE_LOCATION);
			WebDriver driver = new SafariDriver();
			
			try {
				WebElement we;
				
				driver.get(URL);
				// must have a short wait to allow time
                             //  for the page to download 
				Thread.sleep(SLEEP_DURATION);


				we = driver.findElement(By.name("names"));
				we.sendKeys(ALIAS_NAME);
				
				we = driver.findElement(By.name("date"));
				we.sendKeys(ALIAS_DATE);
				
				we = driver.findElement(By.name("course_id"));
				we.sendKeys(ALIAS_NAME);
				
				
				// find and click the submit button
				we = driver.findElement(By.id("submit"));
				we.click();
				Thread.sleep(SLEEP_DURATION);
				
				// verify the correct message
				we = driver.findElement(By.id("message"));
				String message = we.getText();
				assertEquals("not working", message);
				
			
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
				
			} finally {
				driver.quit();
			}

	}
}

