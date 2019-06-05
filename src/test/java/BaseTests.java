import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class BaseTests extends Verifications {
    static String PROD_OPEN_STREET_MAP_URL = "https://api.openstreetmap.org";
    static String DEV_OPEN_STREET_MAP_URL = "https://master.apis.dev.openstreetmap.org";
    private static final Logger LOGGER = Logger.getLogger(BaseTests.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().httpClient(HttpClientConfig
                .httpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", 7000));
    }

    @Parameters({"testEnv"})
    @BeforeTest(alwaysRun = true)
    public void beforeTest(String testEnv){
        RestAssured.baseURI = selectEnvironmentBaseUrl(testEnv);
    }

    @BeforeMethod(alwaysRun = true)
    public void setup (Method method) {
        initTestHelperObjects();
    }

    public Object[][] getDataProvider(String testName) throws IOException {
        String filePath= System.getProperty("user.dir")+"/src/test/dataproviders/"+testName+".csv";
        Reader reader = Files.newBufferedReader(Paths.get(filePath));

        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> found = csvReader.readAll();
        Object[][] dataProviderObj = found.toArray(new Object[found.size()][]);

        return dataProviderObj;
    }

    @Parameters({"testEnv"})
    public String selectEnvironmentBaseUrl(String testEnv) {
        if (testEnv.equalsIgnoreCase("prod")) {
            LOGGER.info(">>Running tests on PROD");
            return PROD_OPEN_STREET_MAP_URL;
        } else {
            LOGGER.info(">>Running tests on DEV");
            return DEV_OPEN_STREET_MAP_URL;
        }
    }


    public abstract void initTestHelperObjects();
}
