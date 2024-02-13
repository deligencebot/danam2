package com.delbot.danam.global.common.aws;

import java.io.InputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.delbot.danam.global.util.CommonUtils;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AwsS3Service {
  //
  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final AmazonS3 amazonS3Client;

  public AwsFileInfo uploadFile(MultipartFile multipartFile) throws IOException {
    validateFileExists(multipartFile);

    String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    InputStream inputStream = multipartFile.getInputStream();
    amazonS3Client.putObject(
              new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                      .withCannedAcl(CannedAccessControlList.PublicRead));
    return new AwsFileInfo(amazonS3Client.getUrl(bucketName, fileName).toString(), fileName);
  }

  public byte[] downloadFile(String fileName) throws Exception {
    S3Object s3Object = amazonS3Client.getObject(bucketName, fileName);
    S3ObjectInputStream inputStream = s3Object.getObjectContent();
    byte[] content = IOUtils.toByteArray(inputStream);
    return content;
  }

  public String deleteFile(String fileName) throws AmazonServiceException {
    amazonS3Client.deleteObject(bucketName, fileName);
    return fileName + " removed ...";
  }

  private void validateFileExists(MultipartFile multipartFile) {
    if (multipartFile.isEmpty()) {
      throw new RuntimeException("file is empty");
    }
  }
}