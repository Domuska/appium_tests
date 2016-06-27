import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;

/**
 * Created by Tomi on 16/06/2016.
 */
public class NotesFaultyTests {

    private AppiumDriver<WebElement> driver;
    WebDriverWait driverWait;
    private String taskListName = "a random task list";
    private String noteName1 = "prepare food";
    private String createNewText = "Create new";

    @Before
    public void setUp() throws Exception{
        File classpathRoot = new File(System.getProperty("user.dir"));
        System.out.println(classpathRoot.getAbsolutePath());
        File app = new File(classpathRoot, "nononsensenotes-debug.apk");
        System.out.println(app.getAbsolutePath());

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "Nexus 5x 1");
        capabilities.setCapability("platformVersion", "6.0.1");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("appPackage", "com.nononsenseapps.notepad");
        capabilities.setCapability("appActivity", ".activities.ActivityList");
//        capabilities.setCapability("fullReset", false);
//        capabilities.setCapability("noReset", false);

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driverWait = new WebDriverWait(driver, 50);
    }


    @After
    public void tearDown() throws Exception{

        if(driver != null)
            driver.quit();
    }

    @Test
    public void testAddNewNoteSearchForFaultyNoteName(){

        closeDrawer();
        createNewNoteWithName(noteName1);
        navigateUp();

        driver.findElement(By.xpath("//*[@text='" + noteName1 + "asdf" +"']"));

        //couple more faulty commands with syntax errors:
//        driver.findElement(By.xpath("//*[@text=" + noteName1+ "]"));
//        driver.findElement(By.xpath("//*[text='" + noteName1+ "']"));

        assertFalse("should have failed before this", true);
    }

    @Test
    public void testSearchForElementWithTextShouldFailOnView(){

        closeDrawer();
        driver.findElement(By.xpath(createNewText)).click();
        assertFalse("should have failed before this", true);
    }


    //search for element with valid ID but that should not be visible
    @Test
    public void testSearchForElementWithIDShouldFailOnView(){
        closeDrawer();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/fab")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/fab")).click();

        assertFalse("should have failed before this", true);
    }

    //search for element with a faulty ID that is not available at all
    @Test
    public void testSearchForElementWithFaultyID(){
        closeDrawer();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/fab" + "asdf")).click();
        assertFalse("should have failed before this", true);
    }

    //search for element with identifier that matches multiple views
    @Test
    public void testSearchForElementWithAmbiguousIdentifier(){

        closeDrawer();

        createNewNoteWithName(noteName1);
        navigateUp();

        createNewNoteWithName(noteName1);
        navigateUp();

        driver.findElement(By.xpath("//*[@text='"+ noteName1 +"']")).click();
        assertFalse("should have failed before this", true);
    }





    // HELPERS

    private void createNotes(String[] noteNames){
        for (int i = 0; i < noteNames.length; i++){
            createNewNoteWithName(noteNames[i]);
            navigateUp();
        }
    }

    private void navigateUp() {
        driver.findElementByAccessibilityId("Navigate up").click();

    }

    private void clickDonebutton() {
        driver.findElement(By.xpath("//*[@text='Done']")).click();
    }

    private void createNewNoteWithName(String name){
        driver.findElement(By.id("com.nononsenseapps.notepad:id/fab")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/taskText")).sendKeys(name);
    }

    private void closeDrawer(){
        WebElement drawerLayout = driver.findElementByAccessibilityId("The drawer layout");

        //find the middle point of the drawer layout
        Dimension dimension = drawerLayout.getSize();
        Point point = new Point(dimension.getWidth()/2, dimension.getHeight()/2);

        //swipe from the middle to left edge to close drawer
        driver.swipe(point.getX(), point.getY(), 1, point.getY(), 300);
    }
}
