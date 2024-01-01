package at.technikum.project.service;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@NoArgsConstructor
public class HttpService {

    public <T> T call(String url, Class<T> clazz) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> result = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), clazz);

        return result.getBody();
    }
}