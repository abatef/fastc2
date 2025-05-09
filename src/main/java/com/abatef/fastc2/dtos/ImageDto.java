package com.abatef.fastc2.dtos;

import com.abatef.fastc2.dtos.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private Integer id;
    private String url;
    private UserDto createdBy;
}
