package az.codeworld.springboot.web.configurations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
// import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import az.codeworld.springboot.admin.dtos.UserDTO;

@EnableCaching
@Configuration
@Profile("prod")
@ConditionalOnClass(RedisCacheManager.class)
public class CacheConfiguration {

    private final Environment environment;

    public CacheConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        //GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        //var valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        JacksonJsonRedisSerializer<Object> valueSerializer = new JacksonJsonRedisSerializer<>(Object.class);

        RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
                                                                            .defaultCacheConfig()
                                                                            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                                                                            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                                                                            //.prefixCacheNameWith("demo:")
                                                                            .disableCachingNullValues()
                                                                            .entryTtl(Duration.ofHours(1));
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("users", defaultConfiguration
                                                            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                                                //new Jackson2JsonRedisSerializer<>(UserDTO.class))
                                                                new JacksonJsonRedisSerializer<>(Object.class))
                                                            )
                                                            .entryTtl(Duration.ofMinutes(10)));
        
        return RedisCacheManager
                                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                                .cacheDefaults(defaultConfiguration)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .transactionAware()
                                .build();
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration("127.0.0.1", 6379);

        standalone.setUsername("default");                    
        standalone.setPassword(RedisPassword.of(environment.getProperty("REDIS_PASSWORD")));

        LettuceClientConfiguration clientCfg = LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(standalone, clientCfg);
    }
}
