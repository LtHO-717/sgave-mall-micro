package com.sgave.mall.sgavemallgoods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemallgoods.dto.FileInfo;
import com.sgave.mall.sgavemallgoods.dto.Goods;
import com.sgave.mall.sgavemallgoods.mapper.FileMapper;
import com.sgave.mall.sgavemallgoods.mapper.GoodsMapper;
import com.sgave.mall.sgavemallgoods.service.GoodsService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private Environment environment;


    @Override
    public Goods saveGoods(Goods goods) throws IllegalArgumentException {
        if (StringUtils.isBlank(goods.getName())) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (StringUtils.isBlank(goods.getGoodsSn())) {
            throw new IllegalArgumentException("商品编号不能为空");
        }
        if (goods.getPrice() == null) {
            throw new IllegalArgumentException("商品价格不能为空");
        }
        goodsMapper.insert(goods);
        //保存商品后，更新缓存
        redisTemplate.opsForValue().set("goods:" + goods.getId(), goods, 60, TimeUnit.MINUTES);
        return goods;
    }

    @Override
    public IPage<Goods> getGoodsList(Integer goodsId, String goodsSn, String name, Integer current, Integer size) {
        Page<Goods> page = new Page<>(current, size);
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(goodsSn)) {
            queryWrapper.like(Goods::getGoodsSn, goodsSn);
        }
        if (!StringUtils.isBlank(name)) {
            queryWrapper.eq(Goods::getName, name);
        }
        if (goodsId != null) {
            queryWrapper.eq(Goods::getId, goodsId);
        }
        queryWrapper.orderByDesc(Goods::getCreateTime);

        return goodsMapper.selectPage(page, queryWrapper);
    }

    @Override
    public int updateGoods(Goods goods) {
        int result = goodsMapper.updateById(goods);
        if (result > 0) {
            // 更新商品后，更新缓存
            redisTemplate.opsForValue().set("goods:" + goods.getId(), goods, 60, TimeUnit.MINUTES);
        }
        return result;

    }

    @Override
    public int updateGoodsShelf(Integer goodsId, Integer status) {
        LambdaUpdateWrapper<Goods> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Goods::getId, goodsId)
                .set(Goods::getStatus, status);
        return goodsMapper.update(lambdaUpdateWrapper);
    }

    private final String UPLOAD_PATH = "D:/files/mall/";

    @Override
    public Object uploadPic(MultipartFile file) {
        /**
         * 将图片存在本地服务器，将图片的访问地址等信息存在数据库
         */
        FileInfo fileInfo = new FileInfo();
        try {
            File dir = new File(UPLOAD_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            Path filePath = Paths.get(UPLOAD_PATH, fileName);
            //StandardCopyOption.REPLACE_EXISTING,是一个用于文件复制操作的选项,
            // 如果目标路径中已经存在文件，使用这个选项可以覆盖该文件
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            //将图片url等信息存数据库
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setFileKey(fileName);
            String host = InetAddress.getLocalHost().getHostAddress();
            // ⚡ 动态获取 application.yml 配置的端口（如 9005）
            String port = environment.getProperty("local.server.port");
            // 返回 localhost 访问
            String url = host + ":" + port + "/mall/" + fileName;
            fileInfo.setUrl(url);
//            fileInfo.setUrl(filePath.toUri().getPath());
            fileInfo.setType(file.getContentType());
            fileInfo.setSize((int) file.getSize());
            fileMapper.insert(fileInfo);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return e.getMessage();
        }
        return fileInfo;
    }

    public String generateUniqueFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random = new Random().nextInt(1000);
        return timestamp + "_" + random + extension;
    }
}
