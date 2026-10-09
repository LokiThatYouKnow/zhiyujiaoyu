package com.mengzhidui.a7springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengzhidui.a7springboot.entity.po.AdminPO;

public interface AdminMapper extends BaseMapper<AdminPO> {

    Boolean checkUsernameExists(String username);
}
