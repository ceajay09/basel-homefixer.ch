package com.example.myproject.contact;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
class ContactRateLimiterTest {
    static class MutableClock extends Clock {
        Instant now=Instant.parse("2026-10-24T12:00:00Z");
        public ZoneId getZone(){return ZoneOffset.UTC;} public Clock withZone(ZoneId zone){return this;} public Instant instant(){return now;}
    }
    @Test void clientWindowExpiresAndClientsAreIndependent(){
        MutableClock clock=new MutableClock(); ContactRateLimiter limiter=new ContactRateLimiter(clock);
        for(int i=0;i<3;i++) assertTrue(limiter.acquire("a")); assertFalse(limiter.acquire("a")); assertTrue(limiter.acquire("b"));
        clock.now=clock.now.plusSeconds(901); assertTrue(limiter.acquire("a"));
    }
    @Test void globalWindowExpires(){
        MutableClock clock=new MutableClock(); ContactRateLimiter limiter=new ContactRateLimiter(clock);
        for(int i=0;i<20;i++) assertTrue(limiter.acquire("client-"+i)); assertFalse(limiter.acquire("new-client"));
        clock.now=clock.now.plusSeconds(3601); assertTrue(limiter.acquire("new-client"));
    }
}
