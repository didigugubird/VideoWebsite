package com.didigugubird.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author author
 * @since 2023-06-23
 */
@Data
@TableName("user_info")
public class UserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    private String nickName;

    private String avatar;

    private Integer exp;

    private Integer coins;

    private Integer likes;

    private Integer playTimes;

    private Integer fanNumbers;

    private Integer followerNumbers;

    private Date birthDate;

    private String sex;

    private String sign;

}
