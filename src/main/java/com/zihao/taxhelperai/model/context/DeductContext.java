package com.zihao.taxhelperai.model.context;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class DeductContext {

    private Long userId;

    private Integer deductType;

    private Date calculateDate;

    private Map<String, Object> detailData = new HashMap<>();

    private BigDecimal calculatedAmount;

    public void put(String key, Object value) {
        detailData.put(key, value);
    }

    public Object get(String key) {
        return detailData.get(key);
    }
}