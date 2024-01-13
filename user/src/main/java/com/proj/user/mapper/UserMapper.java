package com.proj.user.mapper;

import com.proj.user.dto.RegisterRequest;
import com.proj.user.dto.UserResponse;
import com.proj.user.model.NotificationType;
import com.proj.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = NotificationType.class)
public interface UserMapper {

    @Mapping(target = "notificationType",
            expression = "java(NotificationType.getTypeFromName(registerRequest.getNotificationType()))")
    User getUserFromRegisterRequest(RegisterRequest registerRequest);

    @Mapping(target = "notificationType", expression = "java(user.getNotificationType().getName())")
    UserResponse getUserResponseFromUser(User user);
}
