package com.example.mq.testcode;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonTest {

    @Test
    public void checkData(){
        try {
            List<String> baseList =FileUtils.readLines(new File("G:/base.txt"), Charset.forName("UTF-8"));
            List<String> oriDataList =FileUtils.readLines(new File("G:/data.txt"), Charset.forName("UTF-8"));
            List<String> dataList = oriDataList.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());

            baseList.removeAll(dataList);
            if(!CollectionUtils.isEmpty(baseList)){
                FileUtils.writeLines(new File("G:/noData.txt"), baseList, true);
                System.out.print("list:"+ JSONObject.toJSONString(baseList));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
