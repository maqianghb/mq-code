package com.example.mq.base.data;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

@Slf4j
public class HotDsKeyHolder {

    public static final ThreadLocal<String> DB_KEY_HOLDER =new ThreadLocal<>();

    public static void switchDB(String dbKey){
        Assert.assertTrue(StringUtils.isNotEmpty(dbKey));
        DB_KEY_HOLDER.set(dbKey);
    }

    public static String getDBKey(){
        return DB_KEY_HOLDER.get();
    }

    public static void clearDBKey(){
        DB_KEY_HOLDER.remove();
    }
}
