
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.*;

import org.junit.Before;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class NotesTest {


    private AppiumDriver<WebElement> driver;
    WebDriverWait driverWait;
    private final String TASK_LIST_TITLE = "Notes";
    private String taskListName = "a random task list";
    private String noteName1 = "prepare food";
    private String noteName2 = "take dogs out";
    private String noteName3 = "water plants";
    private String noteName4 = "sleep";
    private String[] noteNameList = {noteName1, noteName2, noteName3, noteName4,
                                    "go for a jog", "do some work", "play with the dog",
                                    "work out", "do weird stuff", "read a book", "drink water",
                                    "write a book", "proofread the book", "publish the book",
                                    "ponder life", "build a house", "repair the house", "call contractor",
                                    "write another book", "scrap the book project", "start a blog",
                                    "  ", "     "};


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

        //commands to shut down the app and clear app data between tests
        capabilities.setCapability("fullReset", false);
//        capabilities.setCapability("noReset", false);

        capabilities.setCapability("noReset", true);

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driverWait = new WebDriverWait(driver, 20);
    }


    @After
    public void tearDown() throws Exception{

        if(driver != null)
            driver.quit();
    }


    @Test
    public void testAddTaskListCheckItIsAddedToDrawer(){

        driver.findElement(By.xpath("//android.widget.TextView[contains(@text, 'Create')]")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/titleField")).sendKeys(taskListName);
        driver.findElement(By.id("com.nononsenseapps.notepad:id/dialog_yes")).click();

        driver.findElementByAccessibilityId("Open navigation drawer").click();

        driver.findElement
                (By.xpath("//*[@text='" + taskListName + "']"));

        //should modify this test so that we check the visibility of the element
//        driverWait.until(ExpectedConditions.visibilityOfElementLocated(
//                assertVisibleText(taskListName)
//        ));

    }

    @Test
    public void testAddNewNoteShouldShowNameInNotesScreen(){

        closeDrawer();
        createNewNoteWithName(noteName1);
        navigateUp();


        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName1 + "']")
                )
        );

        //assert the element is visible
        assertVisibleText(noteName1);

    }


    @Test
    public void testAddNewNoteWithDueDateCheckDateIsVisible(){

        closeDrawer();

        createNewNoteWithName(noteName1);
        driver.hideKeyboard();

        driver.findElement(By.id("com.nononsenseapps.notepad:id/dueDateBox")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/done")).click();
        navigateUp();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName1 + "']")
                )
        );

        //should find the element even if its' visibility is set to invisible
        driver.findElement(By.id("com.nononsenseapps.notepad:id/date"));
    }

//    @Test
//    public void testAddNewNoteWithReminderDateAndTime(){
//
//    }

    @Test
    public void testCreateNoteAndDeleteIt(){

        //first wait that the app is actually started
        driverWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("com.nononsenseapps.notepad:id/drawer_layout")
                )
        );

        closeDrawer();
        createNewNoteWithName(noteName1);
        navigateUp();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ noteName1 + "']")
                )
        );

        driver.findElement(By.xpath("//*[@text='"+ noteName1 + "']")).click();

//        driver.findElementByAccessibilityId("Delete").click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/menu_delete")).click();
//        driver.findElement(By.xpath("//*[@text='OK']")).click();
        driver.findElement(By.id("android:id/button1")).click();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ TASK_LIST_TITLE + "']")
                )
        );

        List<WebElement> elements = driver.findElements(By.xpath("//*[@text='"+ noteName1 + "']"));
        assertEquals(0, elements.size());
    }


    @Test
    public void testAddNoteToTaskList(){

        driver.findElement(By.xpath("//*[@text='Create new']")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/titleField")).sendKeys(taskListName);
        driver.findElement(By.id("com.nononsenseapps.notepad:id/dialog_yes")).click();

        //open the note that was created
        openDrawer();
        driver.findElement(By.xpath("//*[@text='"+ taskListName +"']")).click();

        createNewNoteWithName(noteName1);
        navigateUp();

        List<WebElement> elements = driver.findElements(By.xpath("//*[@text='"+ noteName1 +"']"));
        assertEquals(1, elements.size());
    }


    //todo should be ignored? or not?
    @Test
    public void testAddNotesOrderByDueDate(){

        closeDrawer();

        String[] expectedNoteOrder = {noteName3, noteName4, noteName2, noteName1};

        createNewNoteWithName(noteName1);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        /*  not the best solution, here we find the first View in the hierarchy (which should be the current month)
            and select consecutive days for different notes as due date. The method works, somewhat but
            does not seem to work entirely correctly (index 0 doesn't pick the first day of the month, for example)
         */
        driver.findElement(By.xpath("//android.view.View[@index='21']")).click();
        driver.findElement(By.xpath("//*[@text='Done']")).click();
        navigateUp();


        createNewNoteWithName(noteName2);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//android.view.View[@index='15']")).click();
        driver.findElement(By.xpath("//*[@text='Done']")).click();
        navigateUp();

        createNewNoteWithName(noteName3);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//android.view.View[@index='3']")).click();
        driver.findElement(By.xpath("//*[@text='Done']")).click();
        navigateUp();

        createNewNoteWithName(noteName4);
        driver.findElement(By.xpath("//*[@text='Due date']")).click();
        driver.findElement(By.xpath("//android.view.View[@index='8']")).click();
        driver.findElement(By.xpath("//*[@text='Done']")).click();
        navigateUp();


        //order by due date
        driver.findElement(By.id("com.nononsenseapps.notepad:id/menu_sort"));
        driver.findElement(By.xpath("//*[@text='Order by due date']")).click();

        //rely on the fact that in a recyclerview the elements always have the same ID
        List<WebElement> elements = getNotesInNotesList();

        assertEquals(4, elements.size());
        for(int i = 0; i < elements.size(); i++){
            assertEquals(expectedNoteOrder[i], elements.get(i).getText());
        }

    }


    @Test
    public void testCreateTaskListAndDeleteIt(){

        //create the tasklist
        driver.findElement(By.xpath("//*[@text='Create new']")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/titleField")).sendKeys(taskListName);
        driver.findElement(By.id("com.nononsenseapps.notepad:id/dialog_yes")).click();

        openDrawer();

        //delete the tasklist
        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='"+ taskListName + "']")
                )
        );

        //long press the element
        WebElement taskList = driver.findElement(By.xpath("//*[@text='"+ taskListName + "']"));
        TouchAction longPress = new TouchAction(driver);
        longPress.longPress(taskList, 2000).release().perform();


        driver.findElement(By.id("com.nononsenseapps.notepad:id/deleteButton")).click();

        driver.findElement(By.xpath("//*[@text='OK']")).click();

        //get the list of elements with the name that is used to create the element, should be 0
        List<WebElement> elements = driver.findElements(By.xpath("//*[@text='"+ taskListName + "']"));
        assertEquals(0, elements.size());

    }


    @Test
    public void testCompletedTasksAreCleared(){
        closeDrawer();

        String [] noteNames = {noteName1, noteName2, noteName3, noteName4};
        createNotes(noteNames);

        List<WebElement> checkBoxes =  driver.findElements(By.id("com.nononsenseapps.notepad:id/checkbox"));
        checkBoxes.get(1).click();
        checkBoxes.get(3).click();

        driver.findElementByAccessibilityId("More options").click();
        driver.findElement(By.xpath("//*[@text='Clear completed']")).click();
        driver.findElement(By.xpath("//*[@text='OK']")).click();

        List<WebElement> remainingTasks = getNotesInNotesList();
        List<String> noteTitles = new ArrayList<>();
        for(int i = 0; i < noteTitles.size(); i++){
            noteTitles.add(remainingTasks.get(i).getText());
        }

        assert (!noteTitles.contains(noteNames[1]));
        assert (!noteTitles.contains(noteNames[3]));
    }

    //note, this test will fail for now since there's an actual bug in the app
    @Test
    public void testAddBigNumberOfNotesScrollDownAndDeleteOne(){

        closeDrawer();
        createNotes(noteNameList);

        driver.scrollTo(noteNameList[0]);

        driver.findElement(By.xpath("//*[@text='"+ noteNameList[0] + "']")).click();


        driver.findElement(By.id("com.nononsenseapps.notepad:id/menu_delete")).click();
        driver.findElement(By.id("android:id/button1")).click();

        //todo is this really a good way to do this?
        // there are no ways in Appium to assert that something is not visible
        try{
            driver.scrollTo(noteNameList[0]);
        }
        catch(NoSuchElementException e){
            assertNotNull("There should always be an exception", e);
        }


    }



    @Test
    @Ignore
    public void testAddNewNoteWithReminderDateAndTime(){

        closeDrawer();

        createNewNoteWithName(noteName1);

        driver.hideKeyboard();

        driver.findElement(By.id("com.nononsenseapps.notepad:id/notificationAdd")).click();
        driver.findElement(By.id("com.nononsenseapps.notepad:id/notificationDate")).click();

        driverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//*[@text='Done']"))

        );

        driver.findElement(By.xpath("//*[@text='Done']")).click();
        navigateUp();
    }




    // HELPERS

    private By assertVisibleText(String text) {
        return By.xpath("//UIAStaticText[@visible=\"true\" and (@name=\"" + text
                + "\" or @hint=\"" + text + "\" or @label=\"" + text
                + "\" or @value=\"" + text + "\""
                + " or @text=\"" + text + "\"" + ")]");
    }


    private void createNotes(String[] noteNames){
        for (int i = 0; i < noteNames.length; i++){
            createNewNoteWithName(noteNames[i]);
            navigateUp();
        }
    }

    private List<WebElement> getNotesInNotesList() {
        return (List<WebElement>) new ArrayList<WebElement>(driver.findElementsByAccessibilityId("Item title"));
    }

    private void navigateUp() {
        driver.findElementByAccessibilityId("Navigate up").click();

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

    private void openDrawer() {

        driver.findElementByAccessibilityId("Open navigation drawer").click();
    }

//
//    private Point getPointToRightOfDrawer(WebElement element){
//
//        Dimension dimension = element.getSize();
//        //x, y
//        return new Point(dimension.getWidth()/2, dimension.getHeight()/2);
//    }

}
