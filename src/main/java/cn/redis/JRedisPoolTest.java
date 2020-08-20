package cn.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * 描述: redis
 *
 * @author : lhb
 * @date : 2020-08-20 20:33
 */
public class JRedisPoolTest {
    /**
     * 非切片链接池
     */
    private JedisPool jedisPool;

    private String ip = "localhost";

//    private String pwd = "123456";
    /**
     * 构造函数
     */
    private JRedisPoolTest() {
        initialPool();
    }

    private void initialPool() {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();

        //是否启用后进先出, 默认true
        config.setLifo(true);
        //最大空闲连接数, 默认8个
        config.setMaxIdle(8);
        //最大连接数, 默认8个
        config.setMaxTotal(8);
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(-1);
        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        config.setMinEvictableIdleTimeMillis(1800000);
        //最小空闲连接数, 默认0
        config.setMinIdle(0);
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(3);
        //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
        config.setSoftMinEvictableIdleTimeMillis(1800000);
        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(false);
        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(false);


        jedisPool = new JedisPool(config, ip, 6379,1000,null,0);

    }

    private Jedis getResource(){
        //        jedis.auth(pwd);
        return jedisPool.getResource();
    }

    /**
     * 关闭连接
     * @param jedis jedis
     */
    private void returnResource(Jedis jedis){
        if (jedis!=null) {
            jedisPool.close();
        }
    }


    //test
    public static void main(String[] args) {
        JRedisPoolTest pool = new JRedisPoolTest();
        Jedis jedis = pool.getResource();
//        jedis.setnx()
        System.out.println(jedis.get("appNameProgramNamesMap"));
        pool.returnResource(jedis);
    }

}
