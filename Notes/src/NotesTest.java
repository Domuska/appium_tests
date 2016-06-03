
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.junit.Before;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class NotesTest {


    private AppiumDriver<WebElement> driver;

    @Before
    public void setUp() throws Exception{
        File classpathRoot = new File(System.getProperty("user.dir"));
        System.out.println(classpathRoot.getAbsolutePath());
        //System.out.println("");
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
    }

    @After
    public void tearDown() throws Exception{

        if(driver != null)
            driver.quit();
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
