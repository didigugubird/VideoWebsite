package com.didigugubird.controller;

import com.didigugubird.dto.UploadResult;
import com.didigugubird.service.VideoInfoService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;


@RestController
@RequestMapping("/upload/")
public class VideoInfoController {
    @Resource
    VideoInfoService videoInfoService;

    @RequestMapping("checkBigFile")
    public UploadResult<Object> checkBigFile(String fileName, String fileMd5, Integer fileSize, Integer chunkSize,
                                             UriComponentsBuilder builder) {
        return new UploadResult<>(videoInfoService.checkBigFile(fileName, fileMd5, fileSize, chunkSize, builder));

    }

    @SneakyThrows
    @PostMapping("uploadBigFile")
    public UploadResult<String> uploadBigFile(@RequestParam("file") MultipartFile file, Integer chunk, String fileMd5, String chunkMd5) {
        return new UploadResult<>(videoInfoService.uploadBigFile(chunk, fileMd5, chunkMd5, file.getBytes()));
    }

    @PostMapping("mergeBigFile")
    public UploadResult<String> mergeBigFIle(String fileMd5, String fileName, UriComponentsBuilder builder) {
        return new UploadResult<>(videoInfoService.mergeBigFile(fileMd5, fileName, builder));

    }
}
