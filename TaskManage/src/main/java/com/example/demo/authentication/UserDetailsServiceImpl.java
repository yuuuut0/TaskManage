package com.example.demo.authentication;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ユーザー情報生成
 * 
 * @author shona
 */
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	/** ユーザー情報デーブル repository */
	private final UserRepository repository;
	
	/**
	 * 引数のユーザーIDIDを使ってDBへユーザー検索を行い、該当データから後の認証処理で使用するユーザー情報を生成します。
	 * 
	 * @param username ログインID
	 * @throws UsernameNotFoundException DB検索でユーザーが見つからなかった場合
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		var userInfo = repository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
		
		return User.withUsername(userInfo.getUserId())
				.password(userInfo.getPassword())
				//.roles("USER")
				//.authorities(userInfo.getAuthorityKind().getCode())
				.build();
	}

}
