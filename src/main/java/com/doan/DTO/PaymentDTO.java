package com.doan.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Integer id;
    private String user;
    private String author;
    private Integer storyId;
    private Integer chapterId;
    private Integer chargeRate;
    private Integer amount;
    private LocalDateTime paymentDate;
}
