package com.sgave.mall.sgavemallshopping.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemallshopping.constant.OrderConstant;
import com.sgave.mall.sgavemallshopping.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallshopping.mapper.GoodsMapper;
import com.sgave.mall.sgavemallshopping.mapper.OrderItemMapper;
import com.sgave.mall.sgavemallshopping.mapper.OrderMapper;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.pojo.Order;
import com.sgave.mall.sgavemallshopping.pojo.OrderItem;
import com.sgave.mall.sgavemallshopping.remote.facade.InventoryRemoteFacade;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OrderTimeoutTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private InventoryRemoteFacade inventoryRemoteFacade;

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void closeUnpaidOrders() {
        // 当前时间减去30分钟，获取超时截止点
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(15);

        // 查询未支付 + 超时的订单
        List<Order> timeoutOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, OrderConstant.STATUS_INIT) // 未支付状态
                .lt(Order::getCreateTime, expireTime));

        for (Order order : timeoutOrders) {
            order.setStatus(OrderConstant.STATUS_CANCEL); // 设置为已取消
            order.setCancelTime(new Date());
            orderMapper.updateById(order);

            List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
//            orderItemList.forEach(orderItem -> {
//                Goods goods = goodsMapper.selectById(orderItem.getProductId());
//                if (goods != null) {
//                    // 获取锁对象
//                    RLock lock = redissonClient.getLock("stock-lock-" + goods.getId());
//
//                    try {
//                        // 尝试获取锁，等待 10 秒，持有锁 30 秒
//                        boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
//                        if (isLocked) {
//                            System.out.println("成功获取到锁");
//                            goods.setStock(goods.getStock() + orderItem.getQuantity());
//                            //更新库存
//                            goodsMapper.updateById(goods);
//                        } else {
//                            System.out.println("未能获取到锁");
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } finally {
//                        // 释放锁
//                        if (lock.isHeldByCurrentThread()) {
//                            lock.unlock();
//                            System.out.println("锁已释放");
//                        }
//                    }
//                }
//            });
            List<RLock> acquiredLocks = new ArrayList<>();
            List<InventoryLockDTO> inventoryLockDTOList = new ArrayList<>();
            try {
                for (OrderItem orderItem : orderItemList) {
                    Goods good = goodsMapper.selectById(orderItem.getProductId());
                    if (good == null) {
                        throw new RuntimeException("商品不存在，ProductId：" + orderItem.getProductId());
                    }
                    // 分布式锁
                    RLock lock = redissonClient.getLock("stock-lock-" + good.getId());
                    // 尝试加锁
                    boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
                    if (!isLocked) {
                        throw new RuntimeException("未能获取到库存锁，商品ID：" + good.getId());
                    }
                    acquiredLocks.add(lock); // 记录已加锁的，出错需释放

                    // 构建批量锁定 DTO
                    InventoryLockDTO dto = new InventoryLockDTO();
                    dto.setGoodsSn(good.getGoodsSn());
                    dto.setQuantity(orderItem.getQuantity());
                    dto.setOrderNo(order.getOrderNo());
                    inventoryLockDTOList.add(dto);
                }
                //全部锁成功后再批量解锁库存
                Object response = inventoryRemoteFacade.unlockBatch(inventoryLockDTOList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放所有已成功获得的锁
                for (RLock lock : acquiredLocks) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        }
    }
}
