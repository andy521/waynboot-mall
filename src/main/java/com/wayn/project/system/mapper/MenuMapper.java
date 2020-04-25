package com.wayn.project.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wayn.project.system.domain.SysMenu;

import java.util.List;

public interface MenuMapper extends BaseMapper<SysMenu> {
    List<String> selectMenuPermsByUserId(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    List<SysMenu> selectMenuTreeAll();

    List<SysMenu> selectMenuListByUserId(SysMenu menu, Long userId);

    List<SysMenu> selectMenuList(SysMenu menu);
}
