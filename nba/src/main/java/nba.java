import java.io.*;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.simple.parser.JSONParser;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class nba {

    public static void main(String[] args) {
        System.out.println(System.getProperties().get("java.class.path"));

        System.setProperty("webdriver.gecko.driver","C:\\Users\\Valeriya\\nba\\geckodriver.exe");
        //System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"null");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"C:\\Users\\Valeriya\\nba\\logs.txt");

        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        WebDriver driver = new FirefoxDriver(options);

        WebDriverWait wait = new WebDriverWait(driver, 10);

        Document doc;
        JSONParser parser = new JSONParser();
        BufferedWriter writer = null;


        try {

            doc = Jsoup.connect("https://www.nba.com/players").get();
            Element elem = doc.getElementById("__NEXT_DATA__");

            if (elem != null){
                String playerData = elem.dataNodes().get(0).toString();

                try
                {
                    JSONObject obj = (JSONObject) parser.parse(playerData);
                    JSONObject props = (JSONObject) obj.get("props");
                    JSONObject pageProps = (JSONObject) props.get("pageProps");
                    JSONArray players = (JSONArray) pageProps.get("players");

                    for(int i = 0; i <players.size(); i++) {
                        JSONObject current_player = (JSONObject) players.get(i);
                        String current_name = current_player.get("PLAYER_FIRST_NAME").toString();
                        String current_surname = current_player.get("PLAYER_LAST_NAME").toString();
                        String current_id = current_player.get("PERSON_ID").toString();
                        String current_slug = current_player.get("PLAYER_SLUG").toString();
                        String targetName = args[0];
                        String targetSurname = args[1];
                        if (current_name.equals(targetName) && current_surname.equals(targetSurname)) {

                            try {
                                String url = "https://www.nba.com/stats/player/"+current_id+"/?Season=2019-20&SeasonType=Regular%20Season&PerMode=Per40";
                                driver.get(url);
                                try {
                                    Thread.sleep(5000);
                                    //save html code from web site (p.s. web site doesn't work)
                                    WebElement firstResult = wait.until(presenceOfElementLocated(By.xpath("//*")));
                                    String player_page = firstResult.getAttribute("outerHTML");
                                    writer = new BufferedWriter( new FileWriter(".\\player.html"));
                                    writer.write(player_page);

                                    //load html file locally
                                    Document doc1 = Jsoup.parse(new File(".\\player.html"), "UTF-8");

                                    // 3PA
                                    Elements element = doc1.getElementsByClass("nba-stat-table__overflow");
                                    if(element.size() == 0){
                                        System.out.println("No data in table!");
                                        driver.quit();
                                        System.exit(0);
                                    }
                                    Elements rowItems = element.get(0).select("td");
                                    Elements rows = element.get(0).select("tr");
                                    Elements year = element.get(0).getElementsByClass("first");
                                    // name
                                    Elements metaTags = doc1.getElementsByTag("title");
                                    String player = metaTags.text().replace("NBA.com/Stats  | ","");

                                    String s = player;
                                    String[] str = s.split(" ");
                                    String name = str[0];
                                    String surname = str[1];

                                    if(name.equals(args[0]) && surname.equals(args[1])) {

                                        if (rowItems.size() <= 26) {
                                            for (int m = 1; m < rowItems.size() - 1; m++) {
                                                System.out.println(year.get(m).text() + " " + rowItems.get(9).text());
                                            }
                                        }
                                        else {
                                            int j = 0;
                                            for (int k = 1; k < rows.size(); k++) {

                                                System.out.println(year.get(k).text() + " " + rowItems.get(9 + j).text());
                                                j += 26;

                                            }
                                        }
                                    }
                                    else{
                                        System.out.println("Wrong name!");

                                    }


                                } catch (InterruptedException e) {
                                    System.out.println("I was interrupted!");
                                    e.printStackTrace();
                                }

                            } finally {
                                driver.quit();
                                System.exit(0);
                            }

                        }
                    }
                } catch(ParseException pe)
                {
                    System.out.println("position: " + pe.getPosition());
                    System.out.println(pe);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
