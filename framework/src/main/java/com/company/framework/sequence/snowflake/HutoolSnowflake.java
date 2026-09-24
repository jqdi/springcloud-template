package com.company.framework.sequence.snowflake;

import com.company.framework.sequence.SequenceGenerator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.id.IdConstants;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HutoolSnowflake implements SequenceGenerator {

	private final long datacenterIdBits = 5L;
	// 最大支持数据中心节点数0~31，一共32个
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

	private Snowflake snowflake = null;

	public HutoolSnowflake(int serverPort, long datacenterId) {
		String ip = NetUtil.getLocalhostStr();
		// 利用ip+端口保证每个服务的workerId不一致，用于ip一致或者端口一致的不同服务
		long workerId = (NetUtil.ipv4ToLong(ip) + serverPort) % (maxDatacenterId + 1);
		log.info("snowflake init,ip:{},port:{},datacenterId:{},workerId:{}", ip, serverPort, datacenterId,
				workerId);
		snowflake = new Snowflake(workerId, datacenterId);
    }

    public HutoolSnowflake() {
        // hutool的workerId、dataCenterId生成跟ip、进程ID有关
        snowflake = IdConstants.DEFAULT_SNOWFLAKE;
        Object workerIdValue = ReflectUtil.getFieldValue(snowflake, "workerId");
        Object datacenterIdValue = ReflectUtil.getFieldValue(snowflake, "dataCenterId");
        log.info("snowflake init,datacenterId:{},workerId:{}", datacenterIdValue, workerIdValue);
    }

    @Override
	public long nextId() {
		return snowflake.nextId();
	}
}