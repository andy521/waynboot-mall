package com.wayn.project.system.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wayn.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 角色表 sys_role
 */
@Data
@ApiModel("角色实体")
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = -1024364179363548873L;
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    @Excel(name = "角色编号", type = 10)
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Excel(name = "角色名称")
    private String roleName;

    /**
     * 权限字符
     */
    @NotBlank(message = "权限字符不能为空")
    @Excel(name = "权限字符")
    private String roleKey;

    /**
     * 角色排序
     */
    @DecimalMin(value = "0", message = "角色排序不能小于0")
    @Excel(name = "角色排序", type = 10)
    private Integer roleSort;

    /**
     * 角色状态（0正常 1停用）
     */
    @Excel(name = "角色状态", replace = {"启用_0", "禁用_1"})
    private Integer roleStatus;

    /**
     * 关联菜单id集合
     */
    @TableField(exist = false)
    private List<Long> menuIds;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;

    public SysRole(Long roleId) {
        this.roleId = roleId;
    }

    public SysRole() {
    }


    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

}
