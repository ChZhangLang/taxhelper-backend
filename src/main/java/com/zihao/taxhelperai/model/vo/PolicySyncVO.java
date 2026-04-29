package com.zihao.taxhelperai.model.vo;

import lombok.Data;

@Data
public class PolicySyncVO {
    private boolean success;
    private int inserted;
    private String message;

    public static PolicySyncVO success(int inserted) {
        PolicySyncVO vo = new PolicySyncVO();
        vo.setSuccess(true);
        vo.setInserted(inserted);
        vo.setMessage("同步成功，新增" + inserted + "条政策");
        return vo;
    }

    public static PolicySyncVO error(String message) {
        PolicySyncVO vo = new PolicySyncVO();
        vo.setSuccess(false);
        vo.setInserted(0);
        vo.setMessage(message);
        return vo;
    }
}