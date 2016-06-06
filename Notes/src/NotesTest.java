
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import org.junit.Before;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class NotesTest {


    private AppiumDriver<WebElement> driver;
    WebDriverWait driverWait;
    private String taskListName = "a random task list";
    private String noteName = "prepare food";

    @Before
    public void setUp() throws Exception{
        File classpathRoot = new File(System.getProperty("user.dir"));
        System.out.println(classpathRoot.getAbsolutePath());
        //System.out.println("");
        //File appDir = new File(classpathRoot, "");
        //File appDir = new File(classpathRoot, "");
        //System.out.println(appDir.getAbsolutePath());
        File app = new File(classpathRoot, "nononsensenotes-debug-4.apk");
        System.out.println(app.getAbsolutePath());

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "Nexus 5x 1");
        capabilities.setCapability("platformVersion", "6.0.1");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("appPackage", "com.nononsenseapps.notepad");
        capabilities.setCapability("appActivity", ".activities.ActivityList");

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        driverWait = new WebDriverWait(driver, 50);
    }


    @After
    public void tearDown() throws Exception{

        if(driver != null)
            driver.quit();
    }

    /**
     * Use contains parameter in xpath search give incomplete search string
     */
    @Test
    public void testAddTaskListCheckItIsAddedToDrawer(){

        WebElement element = driver.findElement(By.xpath("//android.widget.TextView[contains(@text, 'Create')]"));
        element.click();
        element = driver.findElementByClassName("android.widget.EditText");
        element.sendKeys(taskListName);

        WebElement okButton = driver.findElement(By.xpath("//*[@text='OK']"));
        okButton.click();

        WebElement hamburgerButton = driver.findElementByAccessibilityId("Open navigation drawer");
        hamburgerButton.click();

//        List<WebElement> textViews = driver.findElementsByClassName("android.widget.TextView");
//
//        for(int i = 0; i < textViews.size(); i++){
//            System.out.println(textViews.get(i).getText());
//        }

        driver.findElement(By.xpath("//android.widget.TextView[@text='" + taskListName + "']"));

    }

    @Test
    public void testAddNewNoteShouldShowNameInNotesScreen(){
        swipeDrawerclosed();

        createNewNoteWithName(noteName);
        driver.findElementByAccessibilityId("Navigate up").click();

//        WebDriverWait driverWait = new WebDriverWait(driver, 50);

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName + "']")
                )
        );
        assertNotNull(driver.findElement(By.xpath("//*[@text='"+ noteName + "']")));
    }


    @Test
    public void testAddNewNoteWithReminderDateAndTime(){

        swipeDrawerclosed();

        createNewNoteWithName(noteName);

        driver.hideKeyboard();

        driver.findElement(By.id("com.nononsenseapps.notepad:id/notificationAdd")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/notificationDate")).click();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("//*[@text='Done']"))

        );

        driver.findElement(By.xpath("//*[@text='Done']")).click();
        driver.findElementByAccessibilityId("Navigate up").click();
    }

    @Test
    public void testAddNewNoteWithDueDateCheckDateIsVisible(){

        swipeDrawerclosed();

        createNewNoteWithName(noteName);
        driver.hideKeyboard();

        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//*[@text='Done']")).click();
        driver.findElementByAccessibilityId("Navigate up").click();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName + "']")
                )
        );

        driver.findElement(By.id("com.nononsenseapps.notepad:id/date"));
    }

    @Test
    public void numberUno(){

        WebElement element = driver.findElement(By.xpath("//*[@text='Settings']"));
        element.click();

        List<WebElement> list = driver.findElementsByClassName("android.widget.TextView");
        assertEquals("Current theme", list.get(2).getText());
    }


    private void createNewNoteWithName(String name){
        driver.findElementByAccessibilityId("Floating action button").click();

        WebElement textView = driver.findElement(By.xpath("//*[@text='Note']"));
        textView.sendKeys(name);
    }

    private void swipeDrawerclosed(){
        WebElement drawerLayout = driver.findElementByAccessibilityId("The drawer layout");
        Point point = getPointToRightOfDrawer(drawerLayout);

        driver.swipe(point.getX(), point.getY(), 1, point.getY(), 300);
    }

    private Point getPointToRightOfDrawer(WebElement element){

        Dimension dimension = element.getSize();
        //x, y
        return new Point(dimension.getWidth()/2, dimension.getHeight()/2);
    }

}
