package com.didigugubird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didigugubird.mapper.VideoInfoMapper;
import com.didigugubird.pojo.VideoInfo;
import com.didigugubird.service.VideoInfoService;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.didigugubird.utils.SystemConstants.VIDEO_PATH;

@Service
@Slf4j
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo> implements VideoInfoService {


    @SneakyThrows
    @Override
    public Object checkBigFile(String fileName, String fileMd5, Integer fileSize, Integer chunkSize, UriComponentsBuilder builder) {

        File filePath = new File(VIDEO_PATH, fileMd5);

        if (!filePath.exists()) {
            Files.createDirectories(filePath.toPath());
        }
        File currentFile = new File(filePath, fileName);
        if (currentFile.exists()) {
            // 文件已经被成功上传过,直接返回url
            return builder.path("file").path("/").path(fileMd5).path("/").path(fileName).build().toUriString();
        }
        // 计算文件总分片数
        int chunkNum = (int) Math.ceil(1.0 * fileSize / chunkSize);
        List<Integer> chunks = IntStream.range(0, chunkNum).boxed().collect(Collectors.toList());
        // 得到已上传的文件片号
        List<Integer> chunkNames = Files.walk(filePath.toPath())
                .filter(Files::isRegularFile)
                .map(path -> Integer.parseInt(path.getFileName().toString()))
                .collect(Collectors.toList());
        // 得到缺失的文件片号
        chunks.removeAll(chunkNames);
        return chunks;
    }

    @Override
    @SneakyThrows
    public String uploadBigFile(Integer chunk, String fileMd5, String chunkMd5, byte[] chunkBytes) {

        // 校验分片md5
        String md5 = DigestUtils.md5DigestAsHex(chunkBytes);
        Assert.isTrue(chunkMd5.equals(md5), "分片已损坏,请重新上传");

        File filePath = new File(VIDEO_PATH + fileMd5, String.valueOf(chunk));

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            StreamUtils.copy(chunkBytes, fileOutputStream);
        } catch (Exception e) {
            // 分片传输过程中出现问题,删除当前分片文件
            Files.delete(filePath.toPath());
            throw e;
        }
        return "分片上传成功";
    }

    @Override
    @SneakyThrows
    public String mergeBigFile(String fileMd5, String fileName, UriComponentsBuilder builder) {

        File filePath = new File(VIDEO_PATH, fileMd5);
        File currentFile = new File(filePath, fileName);

        if (currentFile.exists()) {
            // 文件已经被成功合并过,直接返回url
            return builder.path(VIDEO_PATH).path("/").path(fileMd5).path("/").path(fileName).build().toUriString();
        }
        List<Path> chunkFiles = Files.walk(filePath.toPath())
                .filter(Files::isRegularFile)
                .sorted()
                .toList();

        // 分片合并
        @Cleanup
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (Path chunkFile : chunkFiles) {
            byte[] chunkFileBytes = Files.readAllBytes(chunkFile);
            byteArrayOutputStream.write(chunkFileBytes);
            Files.delete(chunkFile);
        }
        byte[] fileBytes = byteArrayOutputStream.toByteArray();
        @Cleanup
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(currentFile));
        StreamUtils.copy(fileBytes, bufferedOutputStream);
        return builder.path(VIDEO_PATH).path("/").path(fileMd5).path("/").path(fileName).build().toUriString();
    }


}
