// AbstractUserDetailsAuthenticationProvider类中的内部类
private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        private DefaultPreAuthenticationChecks() {
        }
		
		// 检查方法
        public void check(UserDetails user) {
			// 帐户未锁定
            if (!user.isAccountNonLocked()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is locked");
                throw new LockedException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
				// 已启用
            } else if (!user.isEnabled()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is disabled");
                throw new DisabledException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
				// 帐户未过期
            } else if (!user.isAccountNonExpired()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account has expired");
                throw new AccountExpiredException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            }
        }
    }