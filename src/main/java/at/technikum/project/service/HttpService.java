package at.technikum.project.service;

public interface HttpService {

    <T> T call(String url, Class<T> clazz);

}
