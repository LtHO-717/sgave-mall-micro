package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemallshopping.constant.OrderConstant;
import com.sgave.mall.sgavemallshopping.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallshopping.mapper.*;
import com.sgave.mall.sgavemallshopping.pojo.*;
import com.sgave.mall.sgavemallshopping.remote.facade.InventoryRemoteFacade;
import com.sgave.mall.sgavemallshopping.service.OrderService;
import com.sgave.mall.sgavemallshopping.service.ShoppingCartService;
import com.sgave.mall.sgavemallshopping.util.OrderHandleOption;
import com.sgave.mall.sgavemallshopping.util.OrderUtil;
import com.sgave.mall.sgavemallshopping.vo.GoodsVO;
import com.sgave.mall.util.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/6/16
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private InventoryRemoteFacade inventoryRemoteFacade;


    /**
     * 查询订单列表
     *
     * @param status 订单状态
     * @param userId 用户id
     */
    @Override
    public Object list(Integer status, Integer userId) {
        // 构造查询订单条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        queryWrapper.orderByDesc(Order::getCreateTime);
        // 查询订单
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        if (orderList.isEmpty()) {
            return ResponseUtil.ok(Collections.emptyList());
        }
        List<Long> orderIds = orderList.stream().map(Order::getId).toList();
        // 一次性查询所有 OrderItem
        List<OrderItem> allOrderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds));

        // 提取所有商品ID
        List<Long> productIds = allOrderItems.stream().map(OrderItem::getProductId).distinct().toList();

        // 一次性查询所有商品
        List<Goods> goodsList = goodsMapper.selectBatchIds(productIds);
        Map<Integer, Goods> goodsMap = goodsList.stream().collect(Collectors.toMap(Goods::getId, g -> g));

        // 构建订单返回对象
        List<Map<String, Object>> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            List<GoodsVO> goodsVOList = new ArrayList<>();
            for (OrderItem item : allOrderItems) {
                if (item.getOrderId().equals(order.getId())) {
                    Goods goods = goodsMap.get(item.getProductId().intValue());
                    if (goods != null) {
                        GoodsVO goodsVO = new GoodsVO();
                        BeanUtils.copyProperties(goods, goodsVO);
                        goodsVO.setNumber(item.getQuantity());
                        goodsVOList.add(goodsVO);
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("orderStatus", order.getStatus());
            map.put("totalAmount", order.getTotalAmount());
            map.put("discountAmount", order.getDiscountAmount());
            map.put("payAmount", order.getPayAmount());
            map.put("payType", order.getPayType());
            map.put("payTime", order.getPayTime());
            map.put("deliveryTime", order.getDeliveryTime());
            map.put("completeTime", order.getCompleteTime());
            map.put("cancelTime", order.getCancelTime());
            map.put("createTime", order.getCreateTime());
            map.put("goodsList", goodsVOList);
            orderVoList.add(map);
        }
        return ResponseUtil.ok(orderVoList);
    }


    /**
     * 订单详情
     *
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public Object detail(Integer userId, Integer orderId) {
        // 查询当前用户的指定订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId).eq(Order::getId, orderId);
        Order order = orderMapper.selectOne(queryWrapper);
        if (null == order) {
            return ResponseUtil.fail(OrderConstant.ORDER_UNKNOWN, "订单不存在");
        }
        // 冗余校验：订单是否属于当前用户
        if (!order.getUserId().equals(Long.valueOf(userId))) {
            return ResponseUtil.fail(OrderConstant.ORDER_INVALID, "不是当前用户的订单");
        }
        // 查询收货地址
        SgaveAddress checkedAddress = addressMapper.selectOne(new LambdaQueryWrapper<SgaveAddress>().eq(SgaveAddress::getUserId, userId).eq(SgaveAddress::getId, order.getAddressId()));
        // 拼接详细收货地址
        String detailedAddress = checkedAddress.getProvince() + checkedAddress.getCity() + checkedAddress.getCounty() + " " + checkedAddress.getAddressDetail();
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderNo());
        orderVo.put("remark", order.getRemark());
        orderVo.put("total_amount", order.getTotalAmount());
        orderVo.put("discount_amount", order.getDiscountAmount());
        orderVo.put("pay_amount", order.getPayAmount());
        orderVo.put("address", detailedAddress);
        orderVo.put("tel", checkedAddress.getTel());
        orderVo.put("pay_type", order.getPayType());
        orderVo.put("delivery_time", order.getDeliveryTime());
        orderVo.put("complete_time", order.getCompleteTime());
        orderVo.put("cancel_time", order.getCancelTime());

        // 查询订单下的商品明细列表
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderItemList);

        return ResponseUtil.ok(result);
    }


    /**
     * 模拟支付
     *
     * @param userId
     * @param orderId
     * @param request
     * @return
     */
    @Override
    public Object simulationPay(Integer userId, String orderId, HttpServletRequest request) {
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        // 查询当前用户的订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId).eq(Order::getId, orderId);
        Order order = orderMapper.selectOne(queryWrapper);

        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            return ResponseUtil.fail(OrderConstant.ORDER_INVALID_OPERATION, "订单不能支付");
        }
        // 模拟支付成功，更新订单状态为“已支付”
        order.setStatus(OrderConstant.STATUS_PAY);
        order.setPayTime(new Date());
        orderMapper.updateById(order);
        return ResponseUtil.ok(order);
    }

    /**
     * 确认收货
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Object confirm(Integer orderId, Integer userId) {
        if (orderId == null) {
            return ResponseUtil.badArgumentValue();
        }
        // 查询订单：确保订单属于当前用户，防止越权操作
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getId, orderId).eq(Order::getUserId, userId));
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(Long.valueOf(userId))) {
            return ResponseUtil.badArgumentValue();
        }

        // 构建订单操作选项，判断是否允许“确认收货”
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            return ResponseUtil.fail(OrderConstant.ORDER_INVALID_OPERATION, "订单不能确认收货");
        }

        // 更新订单状态为“已完成”
        order.setStatus(OrderConstant.STATUS_FINISH);
        order.setCompleteTime(new Date());
        orderMapper.updateById(order);
        return ResponseUtil.ok();
    }

    /**
     * 删除订单信息
     *
     * @param orderId 订单id
     * @param userId  用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object delete(Integer orderId, Integer userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (Objects.equals(order.getStatus(), OrderConstant.STATUS_PAY) || Objects.equals(order.getStatus(), OrderConstant.STATUS_SHIP)) {
            return ResponseUtil.fail(OrderConstant.ORDER_DELETE_FAILED, "订单不能删除");
        }
        //构建订单信息删除条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        //根据userId和orderId删除
        queryWrapper.eq(Order::getUserId, userId).eq(Order::getId, orderId);
        //删除订单信息
        orderMapper.delete(queryWrapper);
        //构建订单商品明细信息删除条件，根据orderId删除
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        //根据userId删除
        wrapper.eq(OrderItem::getOrderId, orderId);
        //删除订单商品明细
        orderItemMapper.delete(wrapper);
        return ResponseUtil.ok();
    }


    /**
     * 保存订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object submit(Integer userId, Integer cartId, Integer addressId, String remark, Integer goodsId, Integer number) {

        List<ShoppingCart> checkedGoodsList = new ArrayList<>();
        //立即购买
        if (goodsId != null && number != null) {
            Goods goods = goodsMapper.selectById(goodsId);
            ShoppingCart tempCart = new ShoppingCart();
            tempCart.setGoodsId(goodsId);
            tempCart.setPrice(goods.getPrice());
            tempCart.setNumber(number);
            checkedGoodsList.add(tempCart);
        } else {
            //购物车提交
            // 货品价格
            if (cartId == null) {
                return ResponseUtil.badArgument();
            }
            if (cartId.equals(0)) {
                checkedGoodsList = shoppingCartService.queryByUidAndChecked(userId);
            } else {
                ShoppingCart cart = shoppingCartMapper.selectById(cartId);
                checkedGoodsList = new ArrayList<>(1);
                checkedGoodsList.add(cart);
            }
        }
        if (checkedGoodsList.isEmpty()) {
            return ResponseUtil.badArgumentValue();
        }
        SgaveAddress checkedAddress = addressMapper.selectOne(new LambdaQueryWrapper<SgaveAddress>().eq(SgaveAddress::getId, addressId).eq(SgaveAddress::getUserId, userId));
        if (checkedAddress == null) {
            return ResponseUtil.badArgument();
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ShoppingCart shoppingCart : checkedGoodsList) {
            // 单个商品的总价 = 商品单价 × 购买数量
            BigDecimal itemTotal = shoppingCart.getPrice().multiply(new BigDecimal(shoppingCart.getNumber()));
            // 累加到总价中
            totalPrice = totalPrice.add(itemTotal);
        }

        Order order = new Order();
        order.setUserId(Long.valueOf(userId));
        order.setOrderNo(this.generateOrderSn(userId));
        order.setStatus(OrderConstant.STATUS_INIT);
        order.setAddressId(checkedAddress.getId());
        order.setPayAmount(totalPrice);
        order.setTotalAmount(totalPrice);
        order.setPayType((short) 1);
        order.setRemark(remark);
        //订单信息入库
        orderMapper.insert(order);
        List<OrderItem> orderItemList = new ArrayList<>();
        // 批量查询商品
        List<Integer> productIds = checkedGoodsList.stream().map(ShoppingCart::getGoodsId).toList();
        List<Goods> goods = goodsMapper.selectBatchIds(productIds);
        // 转为 Map<商品ID, ShoppingCart>
        Map<Integer, ShoppingCart> cartMap = checkedGoodsList.stream().collect(Collectors.toMap(ShoppingCart::getGoodsId, Function.identity()));
        // 将每个商品转换为一个订单项（OrderItem），并从购物车中获取对应数量
        goods.forEach(good -> {
            ShoppingCart cart = cartMap.get(good.getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(Long.valueOf(good.getId()));
            orderItem.setProductName(good.getName());
            orderItem.setPrice(good.getPrice());
            orderItem.setSkuId(1L);
            orderItem.setSkuAttr("红色");
            orderItem.setSubtotal(new BigDecimal(33));
            orderItem.setQuantity(cart != null ? cart.getNumber() : 0);
            orderItemList.add(orderItem);
        });
        //批量插入订单商品明细表
        orderItemMapper.insert(orderItemList);
        //提交订单减少库存


//        orderItemList.forEach(orderItem -> {
//            Goods good = goodsMapper.selectById(orderItem.getProductId());
//            if (good != null) {
//                // 获取锁对象
//                RLock lock = redissonClient.getLock("stock-lock-" + good.getId());
//
//                try {
//                    // 尝试获取锁，等待 10 秒，持有锁 30 秒
//                    boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
//                    if (isLocked) {
//                        System.out.println("成功获取到锁");
//
//                        int currentStock = good.getStock();
//                        int newStock = currentStock - orderItem.getQuantity();
//                        if (newStock < 0 || currentStock <= 0) {
//                            throw new RuntimeException("库存异常，商品ID：" + good.getId());
//                        }
//                        good.setStock(newStock);
//                        //更新库存
//                        goodsMapper.updateById(good);
//                    } else {
//                        System.out.println("未能获取到锁");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    // 释放锁
//                    if (lock.isHeldByCurrentThread()) {
//                        lock.unlock();
//                        System.out.println("锁已释放");
//                    }
//                }
//            }
//        });

        List<RLock> acquiredLocks = new ArrayList<>();
        List<InventoryLockDTO> inventoryLockDTOList = new ArrayList<>();
        try {
            for (OrderItem orderItem : orderItemList) {
                Goods good = goodsMapper.selectById(orderItem.getProductId());
                if (good == null) {
                    return ResponseUtil.fail(601, "商品不存在");
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
            //全部锁成功后再批量锁库存
            Object response = inventoryRemoteFacade.lockBatch(inventoryLockDTOList);
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
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderItemList", orderItemList);
        return ResponseUtil.ok(data);
    }

    /**
     * 结算商品
     *
     * @param cartId    购物车id
     * @param addressId 地址id
     * @param userId    用户id
     */
    @Override
    public Object checkout(Integer cartId, Integer addressId, Integer userId, Integer goodsId, Integer number) {
        // 收货地址
        SgaveAddress checkedAddress = null;
        if (addressId == null || addressId.equals(0)) {
            // 查找该用户的默认地址
            checkedAddress = findDefault(userId);
            // 如果仍然没有地址，则是没有收货地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                checkedAddress = new SgaveAddress();
                checkedAddress.setId(0);
                addressId = 0;
            } else {
                // 如果找到默认地址，获取其id
                addressId = checkedAddress.getId();
            }
        } else {
            // 如果传入了具体地址ID，查询该地址是否属于当前用户
            LambdaQueryWrapper<SgaveAddress> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SgaveAddress::getId, addressId).eq(SgaveAddress::getUserId, userId);
            checkedAddress = addressMapper.selectOne(queryWrapper);
            // 如果null, 则报错
            if (checkedAddress == null) {
                return ResponseUtil.badArgumentValue();
            }
        }
        Map<String, Object> data = new HashMap<>();
        List<ShoppingCart> checkedGoodsList = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        //立即购买
        if (goodsId != null && number != null) {
            Goods goods = goodsMapper.selectById(goodsId);
            totalPrice = goods.getPrice().multiply(new BigDecimal(number));
            ShoppingCart tempCart = new ShoppingCart();
            tempCart.setGoodsId(goodsId);
            tempCart.setGoodsName(goods.getName());
            tempCart.setPrice(goods.getPrice());
            tempCart.setNumber(number);
            tempCart.setPicUrl(goods.getPicUrl());
            checkedGoodsList.add(tempCart);
        } else {
            //购物车结算
            // 商品价格
            //如果购物车商品ID是空，则下单当前用户所有购物车商品
            if (cartId == null || cartId.equals(0)) {
                checkedGoodsList = this.queryByUidAndChecked(userId);
            } else {
                //如果购物车商品ID非空，则只下单当前购物车商品
                ShoppingCart cart = this.findById(userId, cartId);
                if (cart == null) {
                    return ResponseUtil.badArgumentValue();
                }
                checkedGoodsList = new ArrayList<>(1);
                checkedGoodsList.add(cart);
            }
            // 定义商品总价，初始为0

            for (ShoppingCart shoppingCart : checkedGoodsList) {
                // 单个商品的总价 = 商品单价 × 购买数量
                BigDecimal itemTotal = shoppingCart.getPrice().multiply(new BigDecimal(shoppingCart.getNumber()));
                // 累加到总价中
                totalPrice = totalPrice.add(itemTotal);
            }
        }
        data.put("checkedGoodsList", checkedGoodsList);
        data.put("totalPrice", totalPrice);
        data.put("checkedAddress", checkedAddress);
        // 返回封装后的结果（包含商品、地址、总价）
        return ResponseUtil.ok(data);
    }

    /**
     * 退款
     *
     * @param orderId 订单id
     * @param userId  用户id
     */
    @Override
    public Object refund(Integer orderId, Integer userId) {
        //根据订单ID和用户ID查询订单，防止越权
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId, orderId).eq(Order::getUserId, userId);
        Order order = orderMapper.selectOne(queryWrapper);
        if (order == null) {
            return ResponseUtil.fail(404, "订单不存在");
        }
        if (order.getStatus().equals(OrderConstant.STATUS_SHIP)) {
            return ResponseUtil.fail(627, "已发货订单不能退款");
        }
        // 修改订单状态为“退款中”
        order.setStatus(OrderConstant.STATUS_REFUNDING);
        order.setCompleteTime(new Date());
        // 更新订单记录
        orderMapper.updateById(order);
        return ResponseUtil.ok();
    }


    /**
     * 取消订单
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object canal(Integer orderId, Integer userId) {
        // 查询订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId, orderId).eq(Order::getUserId, userId);
        Order order = orderMapper.selectOne(queryWrapper);

        // 修改订单状态为已取消
        order.setStatus(OrderConstant.STATUS_CANCEL);
        order.setCancelTime(new Date());
        orderMapper.updateById(order);

        // 查询订单商品明细
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        // 遍历订单商品，更新库存
//        orderItemList.forEach(orderItem -> {
//            Goods good = goodsMapper.selectById(orderItem.getProductId());
//            if (good != null) {
//                // 获取锁对象
//                RLock lock = redissonClient.getLock("stock-lock-" + good.getId());
//
//                try {
//                    // 尝试获取锁，等待 10 秒，持有锁 30 秒
//                    boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
//                    if (isLocked) {
//                        System.out.println("成功获取到锁");
//                        good.setStock(good.getStock() + orderItem.getQuantity());
//                        //更新库存
//                        goodsMapper.updateById(good);
//                    } else {
//                        System.out.println("未能获取到锁");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    // 释放锁
//                    if (lock.isHeldByCurrentThread()) {
//                        lock.unlock();
//                        System.out.println("锁已释放");
//                    }
//                }
//            }
//        });

        List<RLock> acquiredLocks = new ArrayList<>();
        List<InventoryLockDTO> inventoryLockDTOList = new ArrayList<>();
        try {
            for (OrderItem orderItem : orderItemList) {
                Goods good = goodsMapper.selectById(orderItem.getProductId());
                if (good == null) {
                    return ResponseUtil.fail(601, "商品不存在");
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
        return ResponseUtil.ok();
    }


    /**
     * 根据用户ID和购物车ID查询单个购物车项
     *
     * @param userId 用户id
     * @param cartId 购物车id
     */
    private ShoppingCart findById(Integer userId, Integer cartId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId).eq(ShoppingCart::getId, cartId);
        return shoppingCartMapper.selectOne(queryWrapper);
    }


    /**
     * 查询指定用户的默认收货地址
     *
     * @param userId 用户id
     */
    private SgaveAddress findDefault(Integer userId) {
        LambdaQueryWrapper<SgaveAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SgaveAddress::getUserId, userId).eq(SgaveAddress::getIsDefault, 1);
        return addressMapper.selectOne(queryWrapper);
    }


    /**
     * 查询当前用户所有已勾选的购物车商品
     *
     * @param userId 用户id
     */
    private List<ShoppingCart> queryByUidAndChecked(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId).eq(ShoppingCart::getCheckStatus, 1);
        return shoppingCartMapper.selectList(queryWrapper);
    }


    /**
     * 生成唯一订单号（OrderSn），格式：yyyyMMdd + 6位随机数字。
     * 若生成的订单号已存在，则循环重新生成，直到唯一为止。
     */
    private String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String orderSn = now + getRandomNum(6);
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = now + getRandomNum(6);
        }
        return orderSn;
    }

    /**
     * 生成指定长度的随机数字字符串（纯数字）
     *
     * @param num 需要生成的位数
     * @return 返回随机数字字符串，例如 "928374"
     */
    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 根据用户 ID 和订单编号统计订单数量，用于判断订单号是否已存在。
     *
     * @param userId  用户 ID
     * @param orderSn 订单编号（OrderSn）
     * @return 查询到的订单数量（通常为 0 或 1）
     */
    public int countByOrderSn(Integer userId, String orderSn) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderSn).eq(Order::getUserId, userId);
        return Math.toIntExact(orderMapper.selectCount(queryWrapper));
    }
}
