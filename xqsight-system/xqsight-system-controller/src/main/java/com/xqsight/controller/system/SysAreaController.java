/**
 * 新启工作室
 * Copyright (c) 1994-2015 All Rights Reserved.
 */
package com.xqsight.controller.system;

import com.xqsight.common.base.controller.BaseTreeController;
import com.xqsight.common.core.orm.MatchType;
import com.xqsight.common.core.orm.PropertyFilter;
import com.xqsight.common.core.orm.PropertyType;
import com.xqsight.common.core.orm.builder.PropertyFilterBuilder;
import com.xqsight.common.exception.GlobalException;
import com.xqsight.common.model.constants.ErrorMessageConstants;
import com.xqsight.system.model.SysArea;
import com.xqsight.system.model.SysMenu;
import com.xqsight.system.service.SysAreaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>区域表 controller</p>
 * <p>Table: sys_area - 区域表</p>
 *
 * @author wangganggang
 * @since 2017-04-05 05:21:08
 */
@RestController
@RequestMapping("/sys/area")
public class SysAreaController extends BaseTreeController<SysAreaService, SysArea, Long> {

    @Override
    protected void preDelete(SysArea sysArea) {
        List<PropertyFilter> propertyFilters = PropertyFilterBuilder.create().matchTye(MatchType.EQ)
                .propertyType(PropertyType.L).add("parent_id", "" + sysArea.getId()).end();
        List<SysArea> sysAreas = service.getByFilters(propertyFilters);
        if (sysAreas != null && sysAreas.size() > 0) {
            throw new GlobalException(ErrorMessageConstants.ERROR_10000, "该项还有子项不可删除");
        }
    }
}