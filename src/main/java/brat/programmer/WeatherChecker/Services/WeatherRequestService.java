package brat.programmer.WeatherChecker.Services;

import brat.programmer.WeatherChecker.Models.WeatherRequest;
import brat.programmer.WeatherChecker.Repositories.WeatherRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherRequestService {

    private final WeatherRequestRepository weatherRequestRepository;

    public void createRequest(String longitude, String latitude, String description, Double temperature, Double temperatureFeelsLike) {
        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setRequestDate(new Date());
        weatherRequest.setLongitude(longitude);
        weatherRequest.setLatitude(latitude);
        weatherRequest.setDescription(description);
        weatherRequest.setTemperature(temperature);
        weatherRequest.setTemperatureFeelsLike(temperatureFeelsLike);

        weatherRequestRepository.save(weatherRequest);
    }

    public List<String> getHistoryList() {
        return weatherRequestRepository.findAllByOrderByRequestDateDesc().stream().map(WeatherRequest::getStringForList).toList();
    }

}
