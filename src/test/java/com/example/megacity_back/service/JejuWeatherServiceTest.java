package com.example.megacity_back.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.hamcrest.Matchers.containsString;

class JejuWeatherServiceTest {
    @Test
    void cachesSuccessfulGridResponseWithoutRepeatingUpstreamCalls() {
        var service = new JejuWeatherService();
        ReflectionTestUtils.setField(service, "kmaKey", "test-key");
        var rest = (RestTemplate) ReflectionTestUtils.getField(service, "rest");
        var server = MockRestServiceServer.bindTo(rest).build();
        server.expect(requestTo(containsString("getUltraSrtNcst"))).andRespond(withSuccess("""
                {"response":{"header":{"resultCode":"00"},"body":{"items":{"item":[
                {"category":"T1H","obsrValue":"25"}]}}}}
                """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(containsString("getUltraSrtFcst"))).andRespond(withSuccess("""
                {"response":{"header":{"resultCode":"00"},"body":{"items":{"item":[
                {"category":"SKY","fcstDate":"20260908","fcstTime":"1200","fcstValue":"1"}]}}}}
                """, MediaType.APPLICATION_JSON));
        var first = service.fetchMergedNow(53, 38);
        assertThat(first.get("success")).isEqualTo(true);
        assertThat(first.get("ncst_tempC")).isEqualTo(25.0);
        assertThat(service.fetchMergedNow(53, 38)).isSameAs(first);
        server.verify();
    }

    @Test
    void missingKeyDoesNotCallUpstreamAndInvalidGridIsRejected() {
        var service = new JejuWeatherService();
        assertThat(service.fetchMergedNow().get("reason")).isEqualTo("missing_key");
        assertThatThrownBy(() -> service.fetchMergedNow(0, 38)).isInstanceOf(IllegalArgumentException.class);
    }
}
