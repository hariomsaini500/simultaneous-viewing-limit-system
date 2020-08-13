package com.skb.checkservice.config;

import com.skb.checkservice.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration(proxyBeanMethods = true)
@EnableRedisRepositories
public class redisConfig {

    private final ClusterConfigurationProperties clusterConfigurationProperties;

    public redisConfig(ClusterConfigurationProperties clusterConfigurationProperties) {
        this.clusterConfigurationProperties = clusterConfigurationProperties;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();

        clusterConfigurationProperties.getNodes().forEach(s -> {
            //parse host and port
            String[] url = s.split(":");
            redisClusterConfiguration.clusterNode(url[0], Integer.parseInt(url[1]));
        });

        return new JedisConnectionFactory(redisClusterConfiguration);
    }

    @Bean
    public RedisTemplate<String, User> redisTemplate() {
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(this.redisConnectionFactory());

        //if default serializer == JdkSerializationRedisSerializer -> unicode error occur
        //new Jackson2JsonRedisSerializer<>()  -> json 포맷으로 데이터를 저장하는 경우

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));

        return redisTemplate;
    }


}
