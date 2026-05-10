package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zihao.taxhelperai.model.entity.TaxMessage;
import org.apache.ibatis.annotations.Param;

/**
 * 税务消息Mapper接口
 */
public interface TaxMessageMapper extends BaseMapper<TaxMessage> {

    /**
     * 分页查询用户消息
     */
    IPage<TaxMessage> selectUserMessages(Page<TaxMessage> page,
                                         @Param("userId") Long userId,
                                         @Param("messageType") String messageType,
                                         @Param("isRead") Integer isRead);

    /**
     * 查询未读消息数量
     */
    Long countUnreadMessages(@Param("userId") Long userId);

    /**
     * 批量更新消息为已读
     */
    int batchUpdateReadStatus(@Param("userId") Long userId,
                              @Param("messageIds") java.util.List<Long> messageIds);

    /**
     * 标记所有消息为已读
     */
    int markAllAsRead(@Param("userId") Long userId);
}