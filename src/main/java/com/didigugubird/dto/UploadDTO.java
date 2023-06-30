package com.didigugubird.dto;

import lombok.Data;

@Data
public class UploadDTO {
    private Integer uid;
    private String fileMd5;
    private String fileName;
    private Integer fileSize;
    private Integer chunk;
    private Integer chunkSize;
    private String chunkMd5;
    private byte[] chunkBytes;
}
