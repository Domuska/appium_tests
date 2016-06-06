
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

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class NotesTest {


    private AppiumDriver<WebElement> driver;
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
        File app = new File(classpathRoot, "nononsensenotes-debug-3.apk");
        System.out.println(app.getAbsolutePath());

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "Nexus 5x 1");
        capabilities.setCapability("platformVersion", "6.0.1");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("appPackage", "com.nononsenseapps.notepad");
        capabilities.setCapability("appActivity", ".activities.ActivityList");

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
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



//        WebElement element = driver.findElementByAccessibilityId("Floating action button");
//        element.click();


    }

    private void swipeDrawerclosed(){

        //resource id: com.nononsenseapps.notepad:id/drawer_layout
        WebElement drawerLayout = driver.findElementByAccessibilityId("The drawer layout");
        Point point = getPointToRightOfDrawer(drawerLayout);
        System.out.println("the x: " + point.getX() + " y:" + point.getY());
        driver.swipe(point.getX(), point.getY(), point.getX()-200, point.getY()-200, 700);

    }

    private Point getPointToRightOfDrawer(WebElement element){

        Point upperLeft = element.getLocation();
        Dimension dimension = element.getSize();
        //x, y
        Double width = dimension.getWidth()*1.2;
        return new Point(width.intValue(), dimension.getHeight()/2);

    }


    @Test
    public void numberUno(){

        //superhyvä testi. 5/5.
        //kun jatkat hommia:
        //-avaa toinen intellij projekti, appium_tests/sample-code-master/sample-code....., sieltä kato vähän miten jatketaan

        WebElement element = driver.findElement(By.xpath("//*[@text='Settings']"));
        element.click();

        List<WebElement> list = driver.findElementsByClassName("android.widget.TextView");
        assertEquals("Current theme", list.get(2).getText());
    }


}
