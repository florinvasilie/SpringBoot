package app.server.controller;

import app.server.model.*;
import org.apache.catalina.LifecycleState;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

import static org.apache.tools.ant.types.resources.MultiRootFileSet.SetType.file;

/** Quote quote = restTemplate.getForObject(
 * Created by fvasilie on 11/4/2016.
 */

@Controller
public class TestController {

    @RequestMapping (value = "/test", method = RequestMethod.GET)
    public String test (RestTemplate restTemplate){
//        Quote quote = restTemplate.getForObject(
//					"http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
//
//        System.out.println(quote.getType());
//        System.out.println(quote.getValue().getId());
//        System.out.println(quote.getValue().getQuote());

//        return "test";

        String imagePath = "C:\\Users\\fvasilie\\Desktop\\PDF3.jpg";
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap();
        map.add("file", new FileSystemResource(imagePath));

        HttpHeaders headers = new HttpHeaders();
        headers.set("ocp-apim-subscription-key","4de7ce9644d74ce7a60c56e4e726b8f1");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

//        headers.set("Content-Type", "application/octet-stream");

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new    HttpEntity<LinkedMultiValueMap<String, Object>>(
                map, headers);

//        System.out.println(map.get("file").toString());



//        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

//        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);


        OCR ocr = restTemplate.postForObject("https://api.projectoxford.ai/vision/v1.0/ocr",requestEntity,OCR.class);

        System.out.println(ocr.getRegions().get(0).getLines().get(0).getWords().get(0).getText());
        String result ="";
        for (Region reg : ocr.getRegions()) {
            for (Line line : reg.lines) {
                for (Word word : line.words) {
                    result += word.text + " ";
                }
                result += "\n";
            }
            result += "\n\n";
        }
        System.out.println(result);
        return "test";
    }


}
