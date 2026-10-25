package com.example.myproject.auth;

import java.time.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class LoginRateLimiterTest {
    static class MutableClock extends Clock {
        Instant now=Instant.parse("2026-10-24T12:00:00Z");
        public ZoneId getZone(){return ZoneOffset.UTC;}
        public Clock withZone(ZoneId zone){return this;}
        public Instant instant(){return now;}
    }
    @Test void perAccountLimitSurvivesChangingIpAndExpires() {
        var clock=new MutableClock();var limiter=new LoginRateLimiter(clock);
        for(int i=0;i<5;i++)assertEquals(0,limiter.acquire("ip-"+i,"test@example.com"));
        assertTrue(limiter.acquire("another-ip","test@example.com")>0);
        clock.now=clock.now.plusSeconds(901);assertEquals(0,limiter.acquire("another-ip","test@example.com"));
    }
    @Test void perIpAndGlobalLimitsCannotBeBypassedWithDifferentEmails() {
        var limiter=new LoginRateLimiter(new MutableClock());
        for(int i=0;i<20;i++)assertEquals(0,limiter.acquire("one-peer","mail"+i));
        assertTrue(limiter.acquire("one-peer","new-mail")>0);
        for(int i=20;i<100;i++)assertEquals(0,limiter.acquire("ip"+i,"mail"+i));
        assertTrue(limiter.acquire("fresh-ip","fresh-mail")>0);
    }
}
