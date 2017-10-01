package com.lazerycode.selenium.tests;

import com.lazerycode.selenium.DriverBase;
import com.lazerycode.selenium.config.YamlConfigRunner;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GoogleExampleIT extends DriverBase {

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    @Test
    @Parameters("YAML")
    public void execise1(String YAML) throws Exception {
        Properties properties = new Properties();
        File file = new File("config.properties");
        FileOutputStream fileOut = new FileOutputStream(file);
        String title;
        URL obj = new URL("https://jsonplaceholder.typicode.com/posts");


        List lista = YamlConfigRunner.main(YAML);

        Element my = new Element();

        for (int i = 0; i < lista.size(); i++){
            Map<String, Element> myElem = (Map<String, Element>) lista.get(i);

            Map<String, String> mapa = (Map<String, String>) myElem.values().iterator().next();
            for (int j = 0; j < mapa.size(); j++){
                my.browser = mapa.get("browser");
                my.url = mapa.get("url");
                my.method = mapa.get("method");
            }

            System.setProperty("browser", my.browser);
            WebDriver driver = getDriver();

            driver.get(my.url);
            title = driver.getTitle();
            System.out.println("Page title is: " + title);

            properties.setProperty("given_Url", my.url);
            properties.setProperty("web_domain", getDomainName(my.url));
            properties.setProperty("title", title);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            if (my.method.toUpperCase().equals("GET")) {
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + obj.toString());
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                System.out.println(response.toString());
            } else {
                String urlParameters = "{\"url\": \""+my.url+"\",\"title\" : \""+title+"\"}";

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + obj.toString());
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                System.out.println(response.toString());
            }

            try {
                fileOut = new FileOutputStream(file, true);
                properties.store(fileOut, "Output element " + i);
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}