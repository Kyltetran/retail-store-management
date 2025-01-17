package databasesystems.project2;

import redis.clients.jedis.Jedis;

public class RedisConnection {

    private static Jedis jedis = null;

    // Redis connection string
    private static final String redisUrl = "redis://default:TWSKcavOXFoD5J6zbjJ7jFTZq0itlhmS@redis-13307.c98.us-east-1-4.ec2.redns.redis-cloud.com:13307";

    // Method to connect to Redis
    public static void connect() {
        if (jedis == null) {
            jedis = new Jedis(redisUrl);
            System.out.println("Connected to Redis: " + jedis.ping());
        }
    }

    // Method to get the Jedis client
    public static Jedis getJedis() {
        if (jedis == null) {
            connect(); // Ensure connection is established
        }
        return jedis;
    }
}