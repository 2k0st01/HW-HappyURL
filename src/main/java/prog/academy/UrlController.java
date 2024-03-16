package prog.academy;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController

public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String test (){
        return "HelloWorld";
    }

    @GetMapping("/shorten_simple")
    public UrlResultDTO shorten(HttpServletRequest request, 
                                @RequestParam String url,
                                @RequestParam(value = "min", required = false, defaultValue = "30") int min){
        String domain = request.getServerName()+":"+request.getServerPort();
        var urlDTO = new UrlStatDTO();
        urlDTO.setUrl(url);
        urlDTO.setMin(min);

        long id = urlService.saveUrl(urlDTO,domain);


        var result = new UrlResultDTO();
        result.setUrl(urlDTO.getUrl());
        result.setShortUrl("http://"+ domain + "/my/"+ id);
        result.setMin(min);

        return result;
    }

    @PostMapping("/shorten")
    public UrlResultDTO shorten(HttpServletRequest request,@RequestBody UrlDTO urlDTO, @RequestBody int min) {
        String domain = request.getServerName()+":"+request.getServerPort();
        long id = urlService.saveUrl(urlDTO,domain);

        var result = new UrlStatDTO();
        result.setUrl(urlDTO.getUrl());
        result.setMin(min);
        result.setShortUrl("http://"+ domain + "/my/"+ id);
        result.setMin(min);

        return result;
    }

    /*
        302
        Location: https://goto.com
        Cache-Control: no-cache, no-store, must-revalidate
     */

    @GetMapping("my/{id}")
    public ResponseEntity<Void> redirect(@PathVariable("id") Long id) {

        var url = urlService.getUrl(id);

        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("delete/{id}")
    public List<UrlStatDTO> delete(@PathVariable("id") long id) {
        urlService.deleteUrl(id);
        return urlService.getStatistics();
    }

    @GetMapping("stat")
    public List<UrlStatDTO> stat() {
        return urlService.getStatistics();
    }
}
