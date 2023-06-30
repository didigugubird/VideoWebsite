package com.didigugubird.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author author
 * @since 2023-06-23
 */
@Data
@TableName("follows")
public class Follows implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 关注的人的ID
     */
    private Integer followedUser;

    /**
     * 关注状态：是否取关
     */
    private Integer status;

    private LocalDateTime createdAt;

}
