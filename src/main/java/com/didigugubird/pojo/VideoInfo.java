package com.didigugubird.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author author
 * @since 2023-06-21
 */
@Data
@TableName("video_info")
public class VideoInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "dv", type = IdType.AUTO)
    private Integer dv;

    @TableField("uid")
    private Integer uid;

    @TableField("md5")
    private String md5;

    @TableField("name")
    private String name;

    @TableField("cover")
    private String cover;

    @TableField("path")
    private String path;

    @TableField("play_times")
    private Integer playTimes;

    @TableField("like")
    private Integer like;

    @TableField("dislike")
    private Integer dislike;

    @TableField("favorite")
    private Integer favorite;

    @TableField("retweet")
    private Integer retweet;

    @TableField("up_datetime")
    private LocalDateTime upDatetime;

    @TableField("state")
    private Boolean state;


}
