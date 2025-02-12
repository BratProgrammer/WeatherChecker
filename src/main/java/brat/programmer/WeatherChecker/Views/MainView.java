package brat.programmer.WeatherChecker.Views;

import brat.programmer.WeatherChecker.Services.OpenWeatherMapService;
import brat.programmer.WeatherChecker.Services.WeatherRequestService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Route("")
@Component
@Slf4j
@Scope("prototype")
public class MainView extends HorizontalLayout {

    private TextField longitudeTextField;
    private TextField latitudeTextField;
    private H2 weatherLabel;
    private H4 weatherTemperature;
    private Button showWeatherButton;
    private Image weatherImage;
    private H3 historyLabel;
    private ListBox<String> historyList;
    private final OpenWeatherMapService openWeatherMapService;
    private final WeatherRequestService weatherRequestService;

    public MainView(OpenWeatherMapService openWeatherMapService,
                    WeatherRequestService weatherRequestService) {

        this.openWeatherMapService = openWeatherMapService;
        this.weatherRequestService = weatherRequestService;

        // Инициализация компонентов UI
        initializeComponents();

        VerticalLayout getWeatherLayout = createGetWeatherInterfaceLayout();
        VerticalLayout historyLayout = createHistoryInterfaceLayout();

        // Создание серой разделительной полосы
        Div separator = new Div();
        separator.setWidth("2px");
        separator.setHeight(historyList.getHeight());
        separator.getStyle().set("background-color", "lightgray");
        separator.getStyle().set("margin", "0 10px");

        add(
                getWeatherLayout,
                separator,
                historyLayout
        );
    }

    private VerticalLayout createGetWeatherInterfaceLayout() {
        VerticalLayout getWeatherInterfaceLayout = new VerticalLayout();

        // Создание и добавление layout
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.add(longitudeTextField, latitudeTextField);

        getWeatherInterfaceLayout.add(
                inputLayout,
                showWeatherButton,
                weatherLabel,
                weatherTemperature,
                weatherImage
        );

        return getWeatherInterfaceLayout;
    }

    private VerticalLayout createHistoryInterfaceLayout() {
        VerticalLayout historyInterfaceLayout = new VerticalLayout();
        historyInterfaceLayout.add(historyLabel, historyList);
        return historyInterfaceLayout;
    }

    private void initializeComponents() {
        longitudeTextField = new TextField();
        longitudeTextField.setLabel("Долгота");
        longitudeTextField.setPlaceholder("Введите долготу");

        latitudeTextField = new TextField();
        latitudeTextField.setLabel("Широта");
        latitudeTextField.setPlaceholder("Введите широту");

        weatherLabel = new H2("");
        weatherLabel.getElement().getStyle().set("font-weight", "bold");

        weatherTemperature = new H4("");
        weatherTemperature.getElement().getStyle().set("font-weight", "bold");

        weatherImage = new Image();
        weatherImage.setVisible(false);

        historyLabel = new H3("История");
        historyLabel.getElement().getStyle().set("font-weight", "bold");

        historyList = new ListBox<>();
        historyList.setReadOnly(true);
        updateHistoryList();

        showWeatherButton = new Button("Показать погоду!", buttonClickEvent -> handleShowWeatherButtonClick());

    }

    private void handleShowWeatherButtonClick() {
        String longitude = longitudeTextField.getValue();
        String latitude = latitudeTextField.getValue();

        if (isItDoubleNumber(longitude)) {
            Notification.show("Ошибка: Долгота должна быть числом", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        if (isItDoubleNumber(latitude)) {
            Notification.show("Ошибка: Широта должна быть числом", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        if (!isValidLongitude(longitude)) {
            Notification.show("Ошибка: Долгота должна быть в диапазоне от -180 до 180.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        if (!isValidLatitude(latitude)) {
            Notification.show("Ошибка: Широта должна быть в диапазоне от -90 до 90.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        try {
            Map<String, String> weatherData = openWeatherMapService.getWeatherData(longitude, latitude);
            weatherLabel.setText(weatherData.get("description"));
            weatherTemperature.setText(weatherData.get("temp") + " градусов, ощущается как " + weatherData.get("feels_like"));
            weatherImage.setSrc(weatherData.get("icon_src"));
            weatherImage.setVisible(true);
            updateHistoryList();
        } catch (Exception e) {
            log.error(e.getMessage());
            Notification.show("Ошибка при получении данных о погоде", 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void updateHistoryList() {
        historyList.setItems(weatherRequestService.getHistoryList());
    }

    private boolean isItDoubleNumber(String longitude) {
        try {
            Double.parseDouble(longitude);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isValidLongitude(String longitude) {
        try {
            double value = Double.parseDouble(longitude);
            return value >= -180 && value <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidLatitude(String latitude) {
        try {
            double value = Double.parseDouble(latitude);
            return value >= -90 && value <= 90;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
