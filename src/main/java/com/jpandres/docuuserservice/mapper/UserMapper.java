package com.jpandres.docuuserservice.mapper;

import com.jpandres.docuuserservice.data.UserVO;
import com.jpandres.docuuserservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserVO modelToDto(User user);
}
