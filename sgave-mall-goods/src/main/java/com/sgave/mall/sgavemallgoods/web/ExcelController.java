package com.sgave.mall.sgavemallgoods.web;

import com.sgave.mall.sgavemallgoods.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Tag(name = "Excel导入导出")
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Resource
    private ExcelService excelService;

    @Operation(summary = "导入")
    @PostMapping
    public String importExcel(@RequestParam(name = "file") MultipartFile file) {
        try {
            excelService.importExcel(file);
            return "Excel数据导入成功！";
        } catch (Exception e) {
            return "Excel数据导入失败：" + e.getMessage();
        }
    }

    @Operation(summary = "导出")
    @GetMapping
    public Object exportExcel() throws IOException {
        byte[] excelData = excelService.exportExcel();
        return ResponseEntity.ok().header(
                "Content-Disposition", "attachment; filename=products.xlsx").body(excelData);
    }
}
