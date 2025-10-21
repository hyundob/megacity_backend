package com.example.megacity_back.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
public class JejuWeatherService {

    // OpenWeatherMap One Call API 3.0
    private static final String OPENWEATHER_URL = "https://api.openweathermap.org/data/3.0/onecall";
    private static final String OPENWEATHER_API_KEY = "f517b1cad012dff300c568f5f7b74322";
    
    // 제주시 좌표
    private static final double JEJU_LAT = 33.4996;
    private static final double JEJU_LON = 126.5312;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    @Value("${kma.key:}")
    private String kmaKey;

    /** OpenWeatherMap One Call API 3.0을 사용한 날씨 정보 */
    public Map<String, Object> fetchMergedNow() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("success", false);
        out.put("reason", "init");

        try {
            String url = UriComponentsBuilder.fromHttpUrl(OPENWEATHER_URL)
                    .queryParam("lat", JEJU_LAT)
                    .queryParam("lon", JEJU_LON)
                    .queryParam("appid", OPENWEATHER_API_KEY)
                    .queryParam("units", "metric") // 섭씨 온도
                    .queryParam("lang", "kr") // 한국어
                    .build(false)
                    .toUriString();

            String body = rest.getForObject(url, String.class);
            if (body == null || body.isBlank()) {
                out.put("reason", "empty_body");
                return out;
            }

            JsonNode root = om.readTree(body);
            JsonNode current = root.path("current");
            
            if (current.isMissingNode()) {
                out.put("reason", "no_current_data");
                return out;
            }

            // 현재 날씨 데이터 추출
            Double temp = current.path("temp").asDouble();
            Double windSpeed = current.path("wind_speed").asDouble();
            Double windDeg = current.path("wind_deg").asDouble();
            Integer cloudsAll = current.path("clouds").asInt();
            
            JsonNode weatherArray = current.path("weather");
            String weatherMain = "";
            Integer weatherId = null;
            
            if (weatherArray.isArray() && weatherArray.size() > 0) {
                JsonNode weather = weatherArray.get(0);
                weatherMain = weather.path("main").asText("");
                weatherId = weather.path("id").asInt();
            }

            // 실황 데이터 (ncst_)
            out.put("ncst_success", true);
            out.put("ncst_tempC", temp);
            out.put("ncst_windMs", windSpeed);
            out.put("ncst_windDirDeg", windDeg);
            
            // 강수 형태 매핑 (OpenWeatherMap → KMA 형식)
            Integer ptyCode = mapWeatherToPty(weatherMain, weatherId);
            out.put("ncst_ptyCode", ptyCode);
            out.put("ncst_ptyText", mapPty(ptyCode != null ? ptyCode : 0));

            // 예보 데이터 (fcst_) - 하늘 상태
            Integer skyCode = mapWeatherToSky(cloudsAll);
            out.put("fcst_success", true);
            out.put("fcst_skyCode", skyCode);
            out.put("fcst_skyText", mapSky(skyCode != null ? skyCode : 1));
            out.put("fcst_ptyCode", ptyCode);
            out.put("fcst_ptyText", mapPty(ptyCode != null ? ptyCode : 0));

            out.put("success", true);
            out.remove("reason");
            
        } catch (Exception e) {
            out.put("reason", "exception:" + e.getClass().getSimpleName() + ":" + e.getMessage());
        }

        return out;
    }

    // OpenWeatherMap weather condition을 KMA PTY 코드로 매핑
    private Integer mapWeatherToPty(String weatherMain, Integer weatherId) {
        if (weatherId == null) return 0;
        
        // OpenWeatherMap weather codes: https://openweathermap.org/weather-conditions
        // 2xx: Thunderstorm, 3xx: Drizzle, 5xx: Rain, 6xx: Snow, 7xx: Atmosphere, 800: Clear, 80x: Clouds
        
        if (weatherId >= 200 && weatherId < 300) return 1; // 뇌우 → 비
        if (weatherId >= 300 && weatherId < 400) return 5; // 이슬비 → 빗방울
        if (weatherId >= 500 && weatherId < 600) return 1; // 비
        if (weatherId >= 600 && weatherId < 700) {
            // 눈 관련
            if (weatherId == 611 || weatherId == 612 || weatherId == 613 || weatherId == 615 || weatherId == 616) {
                return 2; // 진눈깨비 → 비/눈
            }
            return 3; // 눈
        }
        
        return 0; // 강수 없음
    }

    // OpenWeatherMap clouds percentage를 KMA SKY 코드로 매핑
    private Integer mapWeatherToSky(Integer clouds) {
        if (clouds == null) return 1;
        
        // KMA: 1=맑음, 3=구름많음, 4=흐림
        if (clouds <= 20) return 1; // 맑음
        if (clouds <= 60) return 3; // 구름많음 
        return 4; // 흐림
    }

    // SKY/PTY 매핑 (KMA 형식 유지)
    private static String mapSky(int code){
        return switch (code){
            case 1 -> "맑음";
            case 3 -> "구름많음";
            case 4 -> "흐림";
            default -> "알수없음(" + code + ")";
        };
    }
    
    private static String mapPty(int code){
        return switch (code){
            case 0 -> "없음";
            case 1 -> "비";
            case 2 -> "비/눈";
            case 3 -> "눈";
            case 5 -> "빗방울";
            case 6 -> "빗방울/눈날림";
            case 7 -> "눈날림";
            default -> "알수없음(" + code + ")";
        };
    }
}
