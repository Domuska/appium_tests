
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Ignore;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;


public class NotesTest {


    private AppiumDriver<WebElement> driver;
    WebDriverWait driverWait;
    private String taskListName = "a random task list";
    private String noteName1 = "prepare food";
    private String noteName2 = "take dogs out";
    private String noteName3 = "water plants";
    private String noteName4 = "sleep";

    @Before
    public void setUp() throws Exception{
        File classpathRoot = new File(System.getProperty("user.dir"));
        System.out.println(classpathRoot.getAbsolutePath());
        //System.out.println("");d
        //File appDir = new File(classpathRoot, "");
        //File appDir = new File(classpathRoot, "");
        //System.out.println(appDir.getAbsolutePath());
        File app = new File(classpathRoot, "nononsensenotes-debug.apk");
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

        createNewNoteWithName(noteName1);
        navigateUp();

//        WebDriverWait driverWait = new WebDriverWait(driver, 50);

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName1 + "']")
                )
        );
        assertNotNull(driver.findElement(By.xpath("//*[@text='"+ noteName1 + "']")));
    }


    @Test
    public void testAddNewNoteWithReminderDateAndTime(){

        swipeDrawerclosed();

        createNewNoteWithName(noteName1);

        driver.hideKeyboard();

        driver.findElement(By.id("com.nononsenseapps.notepad:id/notificationAdd")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/notificationDate")).click();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("//*[@text='Done']"))

        );

        clickDonebutton();
        navigateUp();
    }

    @Test
    public void testAddNewNoteWithDueDateCheckDateIsVisible(){

        swipeDrawerclosed();

        createNewNoteWithName(noteName1);
        driver.hideKeyboard();

        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        clickDonebutton();
        navigateUp();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName1 + "']")
                )
        );

        driver.findElement(By.id("com.nononsenseapps.notepad:id/date"));
    }



    @Test
    public void testCreateNoteAndDeleteIt(){

        swipeDrawerclosed();

        createNewNoteWithName(noteName1);
        navigateUp();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName1 + "']")
                )
        );

        driver.findElement(By.xpath("//*[@text='"+ noteName1 + "']")).click();

        driver.findElementByAccessibilityId("Delete").click();
        driver.findElement(By.xpath("//*[@text='OK']")).click();

        List<WebElement> elements = driver.findElements(By.xpath("//*[@text='"+ noteName1 + "']"));

        assertEquals(0, elements.size());

    }



    @Test
    public void testTaskListAddNoteToIt(){

//        driver.findElement(By.id("android:id/text1")).click();
        driver.findElement(By.xpath("//*[@text='Create new']")).click();
        driver.findElement(By.xpath("//*[@text='Title']")).sendKeys(taskListName);
        driver.findElement(By.xpath("//*[@text='OK']")).click();

        createNewNoteWithName(noteName1);
        navigateUp();

        List<WebElement> elements = driver.findElements(By.xpath("//*[@text='"+ noteName1 +"']"));
        assertEquals(1, elements.size());
    }

    @Test
    public void testAddNotesOrderByDueDate(){

        swipeDrawerclosed();

        String[] expectedNoteOrder = {noteName3, noteName4, noteName2, noteName1};


        createNewNoteWithName(noteName1);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        /*  not the best solution, here we find the first View in the hierarchy (which should be the current month)
            and select consecutive days for different notes as due date. The method works, somewhat but
            does not seem to work entirely correctly (index 0 doesn't pick the first day of the month, for example)
         */
        driver.findElement(By.xpath("//android.view.View[@index='21']")).click();
        clickDonebutton();
        navigateUp();


        createNewNoteWithName(noteName2);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//android.view.View[@index='15']")).click();
        clickDonebutton();
        navigateUp();

        createNewNoteWithName(noteName3);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//android.view.View[@index='3']")).click();
        clickDonebutton();
        navigateUp();

        createNewNoteWithName(noteName4);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//android.view.View[@index='8']")).click();
        clickDonebutton();
        navigateUp();


        //order by due date
        driver.findElementByAccessibilityId("Sorting").click();
        driver.findElement(By.xpath("//*[@text='Order by due date']")).click();

        //rely on the fact that in a recyclerview the elements always have the same ID
        List<WebElement> elements = new ArrayList<>(driver.findElementsByAccessibilityId("Item title"));

        assertEquals(4, elements.size());
        for(int i = 0; i < elements.size(); i++){
            assertEquals(expectedNoteOrder[i], elements.get(i).getText());
        }

    }

    //todo finish this
    @Test
    public void testCreateTaskListAndDeleteIt(){

        driver.findElement(By.xpath("//*[@text='Create new']")).click();
    }


    @Test
    @Ignore
    public void numberUno(){

        WebElement element = driver.findElement(By.xpath("//*[@text='Settings']"));
        element.click();

        List<WebElement> list = driver.findElementsByClassName("android.widget.TextView");
        assertEquals("Current theme", list.get(2).getText());
    }






    // HELPERS

    private void navigateUp() {
        driver.findElementByAccessibilityId("Navigate up").click();
    }

    private void clickDonebutton() {
        driver.findElement(By.xpath("//*[@text='Done']")).click();
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
