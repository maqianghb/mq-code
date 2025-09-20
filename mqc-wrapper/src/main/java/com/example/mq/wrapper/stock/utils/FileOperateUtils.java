package com.example.mq.wrapper.stock.utils;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: maqiang
 * @CreateTime: 2025-09-19 23:53:29
 * @Description:
 */
public class FileOperateUtils {

    /**
     * 保存本地文件
     */
    public static void saveLocalFile(String fileName, String rowHeader, List<String> rowDataList, boolean append){
        List<String> fileDataList = Lists.newArrayList();
        fileDataList.add(rowHeader);
        fileDataList.addAll(rowDataList);

        // 记录结果
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDateTime localDateTime = LocalDateTime.now();//当前时间
            String strCurrentDate = df.format(localDateTime);//格式化为字符串

            String formatFileName = StringUtils.substringBeforeLast(fileName, ".") + "_" + strCurrentDate
                    + "." + StringUtils.substringAfterLast(fileName, ".");
            FileUtils.writeLines(new File(formatFileName), "UTF-8", fileDataList, append);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
