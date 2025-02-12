package brat.programmer.WeatherChecker.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenWeatherMapService {

    @Value("${open.weather.api.key}")
    private String API_KEY;

    @Value("${open.weather.api.language}")
    private String language;

    @Value("${open.weather.api.temperature.scale}")
    private String temperatureScale;

    private final WeatherRequestService weatherRequestServices;

    public Map<String, String> getWeatherData(String longitude, String latitude) throws IOException, InterruptedException {

        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=%s&lang=%s&appid=%s", latitude, longitude, temperatureScale, language, API_KEY);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка на успешный ответ
        if (response.statusCode() != 200) {
            throw new IOException("Failed to get weather data: " + response.body());
        }

        // Парсинг JSON-ответа
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());

        // Извлечение необходимых данных
        Map<String, String> weatherData = new HashMap<>();

        String description = jsonNode.path("weather").get(0).path("description").asText();
        String iconId = jsonNode.path("weather").get(0).path("icon").asText();
        Double temperature = Double.parseDouble(jsonNode.path("main").path("temp").asText());
        Double temperatureFeelsLike = Double.parseDouble(jsonNode.path("main").path("feels_like").asText());

        weatherData.put("description", description);
        weatherData.put("temp", temperature.toString());
        weatherData.put("feels_like", temperatureFeelsLike.toString());
        weatherData.put("icon_src", String.format("https://openweathermap.org/img/wn/%s@2x.png", iconId));

        weatherRequestServices.createRequest(longitude, latitude, description, temperature, temperatureFeelsLike);

        return weatherData;
    }

}
