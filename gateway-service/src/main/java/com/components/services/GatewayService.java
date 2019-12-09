package com.components.services;

import com.components.dto.CarDto;
import com.components.entities.Car;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
public class GatewayService {

    private String bookingServiceBackPrefix;
    private String mainServiceBackPrefix;
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @Autowired
    public GatewayService(@Value("${url.booking-service}") String bookingServiceBackPrefix,
                          @Value("${url.main-service}") String mainServiceBackPrefix,
                          RestTemplateBuilder restTemplateBuilder) {
        this.bookingServiceBackPrefix = bookingServiceBackPrefix;
        this.mainServiceBackPrefix = mainServiceBackPrefix;
        this.restTemplate= restTemplateBuilder.build();
    }

    public boolean book(CarDto carDto) {

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject carJsonObject = new JSONObject();
        carJsonObject.put("id", carDto.getId());
        carJsonObject.put("brand", carDto.getBrand());
        carJsonObject.put("model", carDto.getModel());
        carJsonObject.put("color", carDto.getColor());
        carJsonObject.put("engine", carDto.getEngine());
        carJsonObject.put("engineCapacity", carDto.getEngineCapacity());
        carJsonObject.put("price", carDto.getPrice());
        carJsonObject.put("transmission", carDto.getTransmission());
        carJsonObject.put("driveLayout", carDto.getDriveLayout());

        HttpEntity<String> request =
                new HttpEntity<String>(carJsonObject.toString(), headers);

        ResponseEntity<String> responseEntityStr = restTemplate.
                postForEntity(bookingServiceBackPrefix, request, String.class);

        return Boolean.getBoolean(responseEntityStr.toString());
    }


    public List<Car> getAll() {
        try {
            ResponseEntity<List<Car>> answer = restTemplate
                    .exchange(
                            mainServiceBackPrefix,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<Car>>() {});
            return answer.getBody();
        } catch (Exception e) {

        }
        return new ArrayList<>();

    }


    public Car getById(long id) {
        try {
            ResponseEntity<Car> answer = restTemplate
                    .getForEntity(mainServiceBackPrefix +
                            "/" + id, Car.class);
            return answer.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Car();
    }
}