package com.example.megacity_back.hydrogen;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Map;
@RestController @RequestMapping("/api/hydrogen")
public class HydrogenController {
    private final HydrogenContinuousService service;
    private final String commandKey;
    public HydrogenController(HydrogenContinuousService service,@Value("${hydrogen.command-api-key:}") String key) {this.service=service;this.commandKey=key;}
    @GetMapping("/history") public HydrogenContinuousService.History history(
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        @RequestParam(defaultValue="1") int days) {return service.history(from,to,days);}
    @PostMapping("/commands") public HydrogenControl receive(
        @RequestHeader(value="X-Hydrogen-Command-Key",required=false) String key,@RequestBody HydrogenContinuousService.Request request) {
        if(commandKey.isBlank()) throw new HydrogenProblem(503,"지령 수신 키가 설정되지 않았습니다.");
        if(key==null||!MessageDigest.isEqual(commandKey.getBytes(StandardCharsets.UTF_8),key.getBytes(StandardCharsets.UTF_8)))
            throw new HydrogenProblem(401,"지령 수신 인증에 실패했습니다.");
        return service.receive(request);
    }
    @ExceptionHandler(HydrogenProblem.class) public ResponseEntity<?> problem(HydrogenProblem e) {
        return ResponseEntity.status(e.status).body(Map.of("status",e.status,"message",e.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> bad(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("status",400,"message",e.getMessage()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class) public ResponseEntity<?> malformed() {
        return ResponseEntity.badRequest().body(Map.of("status",400,"message","요청 본문과 시각·숫자 형식을 확인하세요."));
    }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> conflict() {
        return ResponseEntity.status(409).body(Map.of("status",409,"message","동일 지령 ID가 이미 처리되었습니다. 같은 요청으로 다시 확인하세요."));
    }
}
