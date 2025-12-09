package com.sgave.mall.sgavemallgoods.service.impl;

import com.sgave.mall.sgavemallgoods.pojo.Goods;
import com.sgave.mall.sgavemallgoods.mapper.GoodsMapper;
import com.sgave.mall.sgavemallgoods.service.ExcelService;
import com.sgave.mall.sgavemallgoods.service.GoodsService;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsService goodsService;

    @Transactional
    @Override
    public void importExcel(MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        // 跳过表头
        if (rows.hasNext()) {
            rows.next();
        }
        while (rows.hasNext()) {
            Goods goods = new Goods();
            Row row = rows.next();
            if (row == null) {
                continue; // 跳过空行
            }
            // 获取第一列 (name)
            Cell nameCell = row.getCell(0);
            String name = "";
            if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
                name = nameCell.getStringCellValue();
            }
            // 获取第二列 (goodsSn)
            Cell goodsSnCell = row.getCell(1);
            String goodsSn = "";
            if (goodsSnCell != null && goodsSnCell.getCellType() == CellType.STRING) {
                goodsSn = goodsSnCell.getStringCellValue();
            }
            // 获取第三列 (unit)
            Cell unitCell = row.getCell(2);
            String unit = "";
            if (unitCell != null && unitCell.getCellType() == CellType.STRING) {
                unit = unitCell.getStringCellValue();
            }

            // 获取第四列 (price)
            Cell priceCell = row.getCell(3);
            Double price = 0.0; // 默认价格为 0.0
            if (priceCell != null && priceCell.getCellType() == CellType.NUMERIC) {
                price = priceCell.getNumericCellValue();
            }

            // 获取第五列 (detail)
            Cell detailCell = row.getCell(4);
            String detail = "";
            if (detailCell != null && detailCell.getCellType() == CellType.STRING) {
                detail = detailCell.getStringCellValue();
            }
            // 获取第六列 (category)
            Cell categoryCell = row.getCell(5);
            String category = "";
            if (categoryCell != null && categoryCell.getCellType() == CellType.STRING) {
                category = categoryCell.getStringCellValue();
            }

            // 如果 name 或 price 为空，则跳过此行
            /*if (name.isEmpty() || price == 0.0) {
                continue; // 跳过当前行
            }*/

            // 设置商品信息
            goods.setName(name);
            goods.setGoodsSn(goodsSn);
            goods.setUnit(unit);
            goods.setPrice(BigDecimal.valueOf(price));
            goods.setDetail(detail);
            goods.setCategory(category);
            // 插入商品数据
            goodsService.saveGoods(goods);
            //goodsMapper.insert(goods);
        }


    }

    @Override
    public byte[] exportExcel() throws IOException{
        Workbook workbook = new XSSFWorkbook();
        //设置导出Excel文件sheet名
        Sheet sheet = workbook.createSheet("商品信息");

        List<Goods> goodsList = goodsMapper.selectList(null);

        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("商品名称");
        headerRow.createCell(2).setCellValue("商品编号");
        headerRow.createCell(3).setCellValue("商品单位");
        headerRow.createCell(4).setCellValue("商品价格");
        headerRow.createCell(5).setCellValue("商品图片");
        headerRow.createCell(6).setCellValue("商品详情");
        headerRow.createCell(7).setCellValue("商品分类");
        headerRow.createCell(8).setCellValue("创建时间");
        headerRow.createCell(9).setCellValue("更新时间");

        CellStyle dateStyle = workbook.createCellStyle();
        short dateFormat = workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss");
        dateStyle.setDataFormat(dateFormat);
        // 填充数据
        int rowIndex = 1;
        for (Goods goods : goodsList) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(goods.getId());
            row.createCell(1).setCellValue(goods.getName());
            row.createCell(2).setCellValue(goods.getGoodsSn());
            row.createCell(3).setCellValue(goods.getUnit());
            row.createCell(4).setCellValue(goods.getPrice().doubleValue());
            row.createCell(5).setCellValue(goods.getPicUrl());
            row.createCell(6).setCellValue(goods.getDetail());
            row.createCell(7).setCellValue(goods.getCategory());
            Cell createCell = row.createCell(8);
            createCell.setCellValue(goods.getCreateTime());  // 设置创建时间
            createCell.setCellStyle(dateStyle);  // 应用日期格式
            Cell updateCell = row.createCell(9);
            updateCell.setCellValue(goods.getUpdateTime());
            updateCell.setCellStyle(dateStyle);
        }
        // 输出Excel文件内容
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

}
