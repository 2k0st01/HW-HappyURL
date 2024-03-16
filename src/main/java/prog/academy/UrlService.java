package prog.academy;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// DB -> E(20) -> R -> S -> DTO <- C -> View / JSON (5)

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public long saveUrl(UrlDTO urlDTO, String link) {
        var urlRecord = urlRepository.findByUrl(urlDTO.getUrl());
        if (urlRecord == null) {
            urlRecord = UrlRecord.of(urlDTO);
            urlRecord.setMin(urlDTO.getMin());
            urlRepository.save(urlRecord);
        }

        return urlRecord.getId();
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void deleteByTime(){
        List<UrlRecord> list = urlRepository.findAll();
        for(UrlRecord url: list){
            if(url.getMin() <= 0){
                urlRepository.deleteById(url.getId());
            }
            url.setMin(url.getMin() - 1);
        }
    }

    @Transactional
    public void deleteUrl(Long id) {
        urlRepository.deleteById(id);
    }

    @Transactional
    public String getUrl(long id) {
        var urlOpt = urlRepository.findById(id);
        if (urlOpt.isEmpty())
            return null;

        var urlRecord = urlOpt.get();
        urlRecord.setCount(urlRecord.getCount() + 1);
        urlRecord.setLastAccess(new Date());

        return urlRecord.getUrl();
    }

    @Transactional(readOnly = true)
    public List<UrlStatDTO> getStatistics() {
        var records = urlRepository.findAll();
        var result = new ArrayList<UrlStatDTO>();

        records.forEach(x -> result.add(x.toStatDTO()));

        return result;
    }
}
