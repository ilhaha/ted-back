/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.exam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.continew.admin.exam.service.CandidateTicketService;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/exam/ticket")
@RequiredArgsConstructor
public class CandidateTicketController {

    private final CandidateTicketService candidateTicketService;

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadTicket(@RequestBody Map<String, String> params) {
        try {
            Long userId = Long.valueOf(params.get("userId"));
            String examNumber = params.get("examNumber");
            // 调用同步方法，直接返回结果
            return candidateTicketService.generateTicket(userId, examNumber);
        } catch (Exception e) {
            String errorMsg = "请求参数错误：" + e.getMessage();
            return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body(errorMsg.getBytes(StandardCharsets.UTF_8));
        }
    }
}
