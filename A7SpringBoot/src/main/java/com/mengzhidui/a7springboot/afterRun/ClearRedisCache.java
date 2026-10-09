package com.mengzhidui.a7springboot.afterRun;
import com.mengzhidui.a7springboot.annotation.ClearAllMenusCache;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ClearRedisCache implements ApplicationRunner {

    @Override
    @ClearAllMenusCache
    public void run(ApplicationArguments args) throws Exception {
    }
}
