package com.bitactor.cloud.spring.sample.common.player;

import com.bitactor.cloud.spring.sample.common.player.bean.NetPlayer;
import com.bitactor.framework.cloud.spring.controller.bean.conn.ConnectManager;

/**
 * @author WXH
 */
public interface PlayerManager extends ConnectManager<Long, NetPlayer> {
}
