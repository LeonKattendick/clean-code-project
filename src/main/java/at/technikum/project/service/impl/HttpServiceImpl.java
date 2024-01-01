package at.technikum.project.service.impl;

import at.technikum.project.service.HttpService;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@NoArgsConstructor
public class HttpServiceImpl implements HttpService {

    @Override
    public <T> T call(String url, Class<T> clazz) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> result = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), clazz);

        return result.getBody();
    }
}