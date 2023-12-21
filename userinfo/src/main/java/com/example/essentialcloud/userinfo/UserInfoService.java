package com.example.essentialcloud.userinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {
    private final ObjectMapper objectMapper;
    private final UserInfoRepository userInfoRepository;
    // inject the template as ValueOperations
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> vOps;

    public UserInfoService(ObjectMapper objectMapper, UserInfoRepository userInfoRepository) {
        this.objectMapper = objectMapper;
        this.userInfoRepository = userInfoRepository;
    }

    public String retrieveUserInfoByAuthenticationId(String authenticationId) throws JsonProcessingException {
        String userInfo = vOps.get(authenticationId);
        if ( userInfo == null ) {
            UserInfo database = userInfoRepository.findUserInfoByAuthenticationId(authenticationId)
                    .orElseThrow(()->new UserInfoNotFoundException("UserInfo not found"));
            userInfo = objectMapper.writeValueAsString(database);
            vOps.set(authenticationId, userInfo);
        }
        return userInfo;
    }
}
