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

/**
 * 기상청 API 백업 파일
 * 화재로 인한 서버 문제 해결 후 다시 사용 예정
 * 백업 날짜: 2025-10-16
 */
//@Service
public class JejuWeatherService_KMA_BACKUP {

    // 초단기 실황/예보
    private static final String NCST_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
    private static final String FCST_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";

    // 제주 격자
    private static final int NX = 52, NY = 38;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    @Value("${kma.key:}")
    private String kmaKey;

    /** 실황(T1H/PTY/VEC/WSD) + 예보(SKY/PTY) 합본 */
    public Map<String, Object> fetchMergedNow() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("success", false);
        out.put("reason", "init");

        String key = normalizeDecodedKey(safeTrim(kmaKey));
        if (key.isEmpty()) {
            out.put("reason", "missing_key");
            return out;
        }

        // ------- 1) 실황: HH00 (매시 10분 이후 권장) -------
        Map<String, Object> ncst = fetchNcstBlock(key);
        out.putAll(prefix("ncst_", ncst));

        // 인증/승인 문제면 더 진행해도 의미 없음
        if (String.valueOf(ncst.get("reason")).contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
            out.put("success", false);
            out.put("reason", ncst.get("reason"));
            return out;
        }

        // ------- 2) 예보: HH30 (매시 45분 이후 권장) -------
        Map<String, Object> fcst = fetchFcstBlock(key);
        out.putAll(prefix("fcst_", fcst));

        // 최종 성공 판정: 어느 쪽이든 주요 값이 있으면 true
        boolean ncstOk = Boolean.TRUE.equals(ncst.get("success"));
        boolean fcstOk = Boolean.TRUE.equals(fcst.get("success"));
        out.put("success", ncstOk || fcstOk);

        if (!(boolean) out.get("success")) {
            // 우선순위로 더 의미 있는 reason 선택
            out.put("reason", fcst.getOrDefault("reason", ncst.get("reason")));
        } else {
            out.remove("reason");
        }
        return out;
    }

    // ---------------- 실황 블록 (T1H/PTY/VEC/WSD) ----------------
    private Map<String, Object> fetchNcstBlock(String key) {
        Map<String, Object> out = base();
        out.put("scope", "ncst");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime base = (now.getMinute() >= 10)
                ? now.withMinute(0).withSecond(0).withNano(0)
                : now.minusHours(1).withMinute(0).withSecond(0).withNano(0);

        for (int i = 0; i < 3; i++) {
            String baseDate = yyyymmdd(base.toLocalDate());
            String baseTime = hhmm(base.toLocalTime());

            String url = UriComponentsBuilder.fromHttpUrl(NCST_URL)
                    .queryParam("serviceKey", key) // 디코딩 키 → 자동 인코딩
                    .queryParam("dataType", "JSON")
                    .queryParam("numOfRows", 200)
                    .queryParam("pageNo", 1)
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime) // HH00
                    .queryParam("nx", NX)
                    .queryParam("ny", NY)
                    .build(false)
                    .toUriString();

            try {
                String body = rest.getForObject(url, String.class);
                if (body == null || body.isBlank()) { out.put("reason", "empty_body"); base = base.minusHours(1); continue; }

                String trimmed = body.trim();
                if (trimmed.startsWith("<")) {
                    String why = extractKmaXmlError(trimmed);
                    out.put("reason", why);
                    if (why.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) return out;
                    base = base.minusHours(1);
                    continue;
                }

                JsonNode root   = om.readTree(body);
                JsonNode header = root.path("response").path("header");
                String code = header.path("resultCode").asText("");
                String msg  = header.path("resultMsg").asText("");
                if (!isOk(code, msg)) { out.put("reason", msg.isBlank() ? "error" : msg); base = base.minusHours(1); continue; }

                JsonNode items = root.path("response").path("body").path("items").path("item");
                if (!items.isArray() || items.size() == 0) { out.put("reason","no_items"); base = base.minusHours(1); continue; }

                Double t1h=null, wsd=null, vec=null; Integer pty=null;
                for (JsonNode it : items) {
                    String cat = it.path("category").asText();
                    String v   = it.path("obsrValue").asText();
                    try {
                        switch (cat) {
                            case "T1H" -> t1h = toD(v);
                            case "WSD" -> wsd = toD(v);
                            case "VEC" -> vec = toD(v);
                            case "PTY" -> pty = toI(v);
                        }
                    } catch (Exception ignore) {}
                }
                if (t1h!=null || wsd!=null || vec!=null || pty!=null) {
                    out.put("success", true);
                    out.put("tempC", t1h);
                    out.put("windMs", wsd);
                    out.put("windDirDeg", vec);
                    out.put("ptyCode", pty);
                    out.put("ptyText", pty==null?null:mapPty(pty));
                    out.put("baseDate", baseDate);
                    out.put("baseTime", baseTime); // HH00
                    return out;
                } else {
                    out.put("reason","no_target_fields");
                }
            } catch (Exception e) {
                out.put("reason", "exception:"+e.getClass().getSimpleName());
            }
            base = base.minusHours(1);
        }
        return out;
    }

    // ---------------- 예보 블록 (SKY/PTY) ----------------
    private Map<String, Object> fetchFcstBlock(String key) {
        Map<String, Object> out = base();
        out.put("scope", "fcst");

        ZonedDateTime nowKst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime base = nowKst.withMinute(30).withSecond(0).withNano(0);
        if (nowKst.getMinute() < 45) base = base.minusHours(1); // HH30, 45분 이후 권장

        for (int i = 0; i < 6; i++) { // 최대 6시간 역방향
            String baseDate = yyyymmdd(base.toLocalDate());
            String baseTime = String.format("%02d30", base.getHour());

            String url = UriComponentsBuilder.fromHttpUrl(FCST_URL)
                    .queryParam("serviceKey", key)
                    .queryParam("dataType", "JSON")
                    .queryParam("numOfRows", 200)
                    .queryParam("pageNo", 1)
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime) // HH30
                    .queryParam("nx", NX)
                    .queryParam("ny", NY)
                    .build(false)
                    .toUriString();

            try {
                String body = rest.getForObject(url, String.class);
                if (body == null || body.isBlank()) { out.put("reason", "empty_body"); base = base.minusHours(1); continue; }

                String trimmed = body.trim();
                if (trimmed.startsWith("<")) {
                    String why = extractKmaXmlError(trimmed);
                    out.put("reason", why);
                    if (why.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) return out;
                    base = base.minusHours(1);
                    continue;
                }

                JsonNode root   = om.readTree(body);
                JsonNode header = root.path("response").path("header");
                String code = header.path("resultCode").asText("");
                String msg  = header.path("resultMsg").asText("");
                if (!isOk(code, msg)) { out.put("reason", msg.isBlank() ? "error" : msg); base = base.minusHours(1); continue; }

                JsonNode items = root.path("response").path("body").path("items").path("item");
                if (!items.isArray() || items.size() == 0) { out.put("reason","no_items"); base = base.minusHours(1); continue; }

                // 모든 SKY/PTY 예보들을 fcstDateTime 기준으로 모아, now에 가장 가까운 시각 선택
                List<Fc> list = new ArrayList<>();
                for (JsonNode it : items) {
                    String cat      = it.path("category").asText();
                    String fcstDate = it.path("fcstDate").asText();
                    String fcstTime = it.path("fcstTime").asText(); // e.g. 1200, 1210 ...
                    String v        = it.path("fcstValue").asText();
                    if (!"SKY".equals(cat) && !"PTY".equals(cat)) continue;

                    LocalDate d = parseDate(fcstDate);
                    LocalTime t = parseTime(fcstTime);
                    if (d == null || t == null) continue;
                    ZonedDateTime z = ZonedDateTime.of(d, t, ZoneId.of("Asia/Seoul"));
                    list.add(new Fc(cat, z, v));
                }
                if (list.isEmpty()) { out.put("reason","no_sky_pty_items"); base = base.minusHours(1); continue; }

                // now 이후 중 가장 가까운 것 우선, 없으면 직전
                list.sort(Comparator.comparing(fc -> fc.when));
                ZonedDateTime target = pickNearestAtOrAfter(list, nowKst);
                // target 시각의 SKY/PTY를 뽑음
                Integer sky=null, pty=null;
                for (Fc fc : list) {
                    if (!fc.when.equals(target)) continue;
                    if ("SKY".equals(fc.cat)) sky = toI(fc.val);
                    if ("PTY".equals(fc.cat)) pty = toI(fc.val);
                }
                // 혹시 SKY/PTY가 한쪽만 target에 없으면 같은 리스트에서 가장 가까운 동일 분류 보완
                if (sky==null) sky = nearestValue(list, "SKY", target);
                if (pty==null) pty = nearestValue(list, "PTY", target);

                if (sky!=null || pty!=null) {
                    out.put("success", true);
                    out.put("skyCode", sky);
                    out.put("skyText", sky==null?null:mapSky(sky));
                    out.put("ptyCode", pty);
                    out.put("ptyText", pty==null?null:mapPty(pty));
                    out.put("baseDate", baseDate);
                    out.put("baseTime", baseTime); // HH30
                    out.put("targetDate", yyyymmdd(target.toLocalDate()));
                    out.put("targetTime", hhmm(target.toLocalTime()));
                    return out;
                } else {
                    out.put("reason","no_target_fields");
                }
            } catch (Exception e) {
                out.put("reason", "exception:"+e.getClass().getSimpleName());
            }
            base = base.minusHours(1);
        }
        return out;
    }

    // --------- helpers ----------
    private static Map<String,Object> base() {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("success", false);
        m.put("reason", "init");
        return m;
    }
    private static Map<String,Object> prefix(String p, Map<String,Object> m){
        Map<String,Object> r = new LinkedHashMap<>();
        for (var e: m.entrySet()) r.put(p+e.getKey(), e.getValue());
        return r;
    }

    private static String safeTrim(String s) {
        if (s == null) return "";
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s.replace("\r","").replace("\n","");
    }
    /** 키가 % 포함이면 한 번 decode해서 디코딩 상태로 맞춘 뒤 사용 */
    private static String normalizeDecodedKey(String k) {
        if (k.contains("%")) {
            try { return URLDecoder.decode(k, StandardCharsets.UTF_8); }
            catch (Exception ignore) {}
        }
        return k;
    }
    private static boolean isOk(String resultCode, String resultMsg) {
        String rc = resultCode == null ? "" : resultCode.trim();
        String rm = resultMsg == null ? "" : resultMsg.trim().toUpperCase();
        return "00".equals(rc) || "0".equals(rc) || rm.contains("NORMAL");
    }
    private static String extractKmaXmlError(String xml) {
        if (xml.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) return "auth_error:SERVICE_KEY_IS_NOT_REGISTERED_ERROR";
        String fault = between(xml, "<faultstring>", "</faultstring>");
        if (fault != null && !fault.isEmpty()) return "soap_error:" + fault;
        String resultMsg = between(xml, "<resultMsg>", "</resultMsg>");
        if (resultMsg != null && !resultMsg.isEmpty()) return "xml_error:" + resultMsg;
        String returnAuthMsg = between(xml, "<returnAuthMsg>", "</returnAuthMsg>");
        if (returnAuthMsg != null && !returnAuthMsg.isEmpty()) return "xml_error:" + returnAuthMsg;
        String reasonCode = between(xml, "<returnReasonCode>", "</returnReasonCode>");
        if (reasonCode != null && !reasonCode.isEmpty()) return "xml_error:code=" + reasonCode;
        String head = xml.replace("\n"," ").replace("\r"," ");
        return "xml_error:" + head.substring(0, Math.min(120, head.length()));
    }
    private static String between(String s, String a, String b){
        int i = s.indexOf(a); if (i<0) return null;
        int j = s.indexOf(b, i+a.length()); if (j<0) return null;
        return s.substring(i+a.length(), j).trim();
    }
    private static String yyyymmdd(LocalDate d){ return String.format("%04d%02d%02d", d.getYear(), d.getMonthValue(), d.getDayOfMonth()); }
    private static String hhmm(LocalTime t){ return String.format("%02d%02d", t.getHour(), t.getMinute()); }
    private static LocalDate parseDate(String s){ try{ return LocalDate.of(Integer.parseInt(s.substring(0,4)), Integer.parseInt(s.substring(4,6)), Integer.parseInt(s.substring(6,8))); }catch(Exception e){ return null; } }
    private static LocalTime parseTime(String s){ try{ return LocalTime.of(Integer.parseInt(s.substring(0,2)), Integer.parseInt(s.substring(2,4))); }catch(Exception e){ return null; } }
    private static Double toD(String v){ try{ return (v==null||v.isBlank())?null:Double.parseDouble(v); }catch(Exception e){ return null; } }
    private static Integer toI(String v){ try{ return (v==null||v.isBlank())?null:Integer.parseInt(v); }catch(Exception e){ return null; } }

    // SKY/PTY 매핑
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

    // 예보 선택 로직
    private static ZonedDateTime pickNearestAtOrAfter(List<Fc> list, ZonedDateTime now){
        // now 이후 중 가장 가까운 것, 없으면 직전(가장 최근 과거)
        Optional<ZonedDateTime> after = list.stream().map(fc -> fc.when).filter(t -> !t.isBefore(now)).min(Comparator.naturalOrder());
        if (after.isPresent()) return after.get();
        return list.get(list.size()-1).when; // 정렬되어 있음
    }
    private static Integer nearestValue(List<Fc> list, String cat, ZonedDateTime target){
        // target과 시간이 같은 것 우선, 없으면 cat 내에서 target과 시간차가 가장 작은 값
        Integer exact = list.stream().filter(fc -> fc.cat.equals(cat) && fc.when.equals(target)).map(fc -> toI(fc.val)).filter(Objects::nonNull).findFirst().orElse(null);
        if (exact != null) return exact;
        return list.stream()
                .filter(fc -> fc.cat.equals(cat))
                .min(Comparator.comparingLong(fc -> Math.abs(Duration.between(fc.when, target).toMinutes())))
                .map(fc -> toI(fc.val)).orElse(null);
    }

    // 예보 아이템 구조체
    private static class Fc {
        final String cat; final ZonedDateTime when; final String val;
        Fc(String c, ZonedDateTime w, String v){ this.cat=c; this.when=w; this.val=v; }
    }
}

