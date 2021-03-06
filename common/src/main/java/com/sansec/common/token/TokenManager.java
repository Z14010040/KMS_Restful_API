package com.sansec.common.token;

import com.sansec.common.exception.GlobalException;
import com.sansec.common.result.CodeMsg;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Decription: 进行token管理
 * @author wangtao
 * create on 2017/12/28.
 */
public final class TokenManager {
	private static final String JWTCERT = "HJDSH83RBNFJDNDSFKM";
	private static final String JWTALG = "AES";
	private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);


	/**
	 * 生成jwt
	 * @param userName  用户名
	 * @param ttlMillis 过期时间间隔
	 *
	 * @return
	 */
	public static String createJWT(String userName, long ttlMillis, String subject, Map<String,Object> map) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		SecretKey secretKey = generalKey();
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		String id = UUID.randomUUID().toString();

		JwtBuilder builder = Jwts.builder()
				//jwtid
				.setId(id)
				//接受者
				.setAudience(userName)
				//主题
				.setSubject(subject)
				//颁发者
				.setIssuer("SecKMS")
				//自定义claim
				.setClaims(map)
				//签发时间
				.setIssuedAt(now)
				//签名算法及密钥
				.signWith(signatureAlgorithm, secretKey);

		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date expDate = new Date(expMillis);
			//过期时间
			builder.setExpiration(expDate);

		}
		String tokenResult = new String();
		tokenResult = Base64Utils.encodeToString(builder.compact().getBytes());
		return tokenResult;
	}
	private static SecretKey generalKey() {

		return new SecretKeySpec(JWTCERT.getBytes(), 0, JWTCERT.length(), JWTALG);

	}


	/**
	 * jwt 解析
	 * @param jwtStr
	 *
	 * @return
	 */
	public static CheckResult validateJWT(String jwtStr) {

		jwtStr = new String(Base64Utils.decodeFromString(jwtStr));


		CheckResult checkResult = new CheckResult();
		Claims claims;
		try {
			claims = parseJWT(jwtStr);
			checkResult.setSuccess(true);
			checkResult.setClaims(claims);
		} catch (ExpiredJwtException e) {
//			logger.error(e.getMessage());
			//token 超出有效期
			throw new GlobalException(CodeMsg.TOKEN_EXPIRED_ERROR.fillArgs(e.getMessage()));
		} catch (SignatureException e) {
//			logger.error(e.getMessage());
			//签名无效
			throw new GlobalException(CodeMsg.TOKEN_SIGNATURE_ERROR.fillArgs(e.getMessage()));
		} catch (Exception e) {
//			logger.error(e.getMessage());
			//其他异常
			throw new GlobalException(CodeMsg.TOKEN_OTHER_ERROR.fillArgs(e.getMessage()));
		}

		return checkResult;

	}

	private static Claims parseJWT(String jwtStr) {
		SecretKey secretKey = generalKey();
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtStr).getBody();
	}
}
