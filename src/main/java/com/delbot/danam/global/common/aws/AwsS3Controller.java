package com.delbot.danam.global.common.aws;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class AwsS3Controller {
  //
  private final AwsS3Service awsS3Service;
}
