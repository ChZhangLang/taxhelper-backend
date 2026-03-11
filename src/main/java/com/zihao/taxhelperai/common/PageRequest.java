package com.zihao.taxhelperai.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.zihao.taxhelperai.constant.CommonConstant;
import lombok.Data;

@Data
@ApiModel(description = "分页请求")
public class PageRequest {

    @ApiModelProperty(value = "当前页号，默认 1", example = "1")
    private int current = 1;

    @ApiModelProperty(value = "页面大小，默认 10", example = "10")
    private int pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序顺序（默认升序）", example = "asc")
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
