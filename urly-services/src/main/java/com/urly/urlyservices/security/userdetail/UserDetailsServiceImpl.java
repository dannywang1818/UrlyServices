package com.urly.urlyservices.security.userdetail;

import com.urly.urlyservices.db.entity.User;
import com.urly.urlyservices.db.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("UserDetailsService Load User");
        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            log.error("User Not Found");
            throw new UsernameNotFoundException(username);
        }

        User user = userOpt.get();
        return UserDetailsImpl.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        log.info("UserDetailsService Load Id");
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.error("User Not Found");
            throw new UsernameNotFoundException(id.toString());
        }

        User user = userOpt.get();
        return UserDetailsImpl.create(user);
    }
}
