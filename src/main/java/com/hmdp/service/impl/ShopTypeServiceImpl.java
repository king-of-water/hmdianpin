package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Key;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ShopType> queryTypeList() {
        String Key = "cache:shop:types";
        // 1.从redis中查询缓存
        String shopTypeJson = stringRedisTemplate.opsForValue().get(Key);
        // 2.判断缓存是否存在
        if (StrUtil.isNotBlank(shopTypeJson)) {
            // 3.缓存命中，直接返回
            List<ShopType> shopTypes = JSONUtil.toList(shopTypeJson, ShopType.class);
            return shopTypes;
        }
        // 4.缓存未命中，查询数据库
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        // 5.写入redis并返回
        if(shopTypes == null){
            return null;
        }
        stringRedisTemplate.opsForValue().set(Key, JSONUtil.toJsonStr(shopTypes));

        return shopTypes;
    }
}
