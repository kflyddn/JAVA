package com.zhonghuasheng.seckill.service;

import com.zhonghuasheng.seckill.domain.OrderInfo;
import com.zhonghuasheng.seckill.domain.SecKillUser;
import com.zhonghuasheng.seckill.domain.SeckillOrder;
import com.zhonghuasheng.seckill.redis.RedisService;
import com.zhonghuasheng.seckill.redis.SeckillKey;
import com.zhonghuasheng.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(SecKillUser user, GoodsVo goods) {
        // 减库存 下订单 写入秒杀订单
        boolean isSuccess = goodsService.reduceStock(goods.getId());
        if (isSuccess) {
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, String.valueOf(goodsId), true);
    }

    public long getSeckillResult(long userId, long goodsId) {
        SeckillOrder order = orderService.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, String.valueOf(goodsId));
    }
}
