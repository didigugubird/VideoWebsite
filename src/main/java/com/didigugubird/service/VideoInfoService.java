package com.didigugubird.service;

import org.springframework.web.util.UriComponentsBuilder;


public interface VideoInfoService {

    /*
     * 大文件上传,检查上传情况
     *
     * @param uploadDTO
     * @param builder
     * @return 若已上传过, 则返回文件访问路径, 否则返回缺失的分片数组
     */

    Object checkBigFile(String fileName, String fileMd5, Integer fileSize, Integer chunkSize,
                        UriComponentsBuilder builder);

    /*
     * 大文件上传,上传分片
     */
    String uploadBigFile(Integer chunk, String fileMd5, String chunkMd5, byte[] chunkBytes);

    /*
     * 大文件上传,合并分片
     * @return 文件访问路径
     */

    String mergeBigFile(String fileMd5, String fileName, UriComponentsBuilder builder);
}
