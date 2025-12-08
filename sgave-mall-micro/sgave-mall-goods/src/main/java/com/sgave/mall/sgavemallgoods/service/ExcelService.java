package com.sgave.mall.sgavemallgoods.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
public interface ExcelService {

    void importExcel(MultipartFile file) throws IOException;

    byte[] exportExcel() throws IOException;
}
