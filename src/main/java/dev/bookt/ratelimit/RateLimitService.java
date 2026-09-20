package dev.bookt.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class RateLimitService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisScript<Long> redisScript;

    @Value("${rate-limit.max-requests}")
    private int maxRequests;

    @Value("${rate-limit.window-seconds}")
    private int windowSeconds;

    public boolean isAllowed(String ip){
        Long requestCount = redisTemplate.execute(redisScript, Collections.singletonList("rate-limit:"+ip), String.valueOf(windowSeconds));

        return requestCount <= maxRequests;
    }


}
