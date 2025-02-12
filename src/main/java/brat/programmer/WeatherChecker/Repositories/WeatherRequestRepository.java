package brat.programmer.WeatherChecker.Repositories;

import brat.programmer.WeatherChecker.Models.WeatherRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherRequestRepository extends JpaRepository<WeatherRequest, Long> {
    List<WeatherRequest> findAllByOrderByRequestDateDesc();

}