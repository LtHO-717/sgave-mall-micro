package com.sgave.mall.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebJwtUtil {

    // 秘钥
    static final String SECRET = "SGAVE-Mall-Token";
    // 签名是有谁生成
    static final String ISSUSER = "SGAVEMALL";
    // 签名的主题
    static final String SUBJECT = "this is SGAVEMall token";
    // 签名的观众
    static final String AUDIENCE = "MINIAPP";

    public static String createToken(Integer userId,String userName) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            Map<String, Object> map = new HashMap<String, Object>();
            Date nowDate = new Date();
            // 过期时间：true 5min false 8h
            Date expireDate = getAfterDate(nowDate, 0, 0, 0, 8, 0, 0);
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            String token = JWT.create()
                    // 设置头部信息 Header
                    .withHeader(map)
                    // 设置 载荷 Payload
                    .withClaim("userId", userId)
                    .withIssuer(ISSUSER)
                    .withSubject(userName)
                    .withAudience(AUDIENCE)
                    // 生成签名的时间
                    .withIssuedAt(nowDate)
                    // 签名过期的时间
                    .withExpiresAt(expireDate)
                    // 签名 Signature
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Integer verifyTokenAndGetUserId(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUSER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            Claim claim = claims.get("userId");
            return claim.asInt();
        } catch (JWTVerificationException exception){
//			exception.printStackTrace();
        }

        return 0;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static Date getAfterDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        if (year != 0) {
            cal.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            cal.add(Calendar.MONTH, month);
        }
        if (day != 0) {
            cal.add(Calendar.DATE, day);
        }
        if (hour != 0) {
            cal.add(Calendar.HOUR_OF_DAY, hour);
        }
        if (minute != 0) {
            cal.add(Calendar.MINUTE, minute);
        }
        if (second != 0) {
            cal.add(Calendar.SECOND, second);
        }
        return cal.getTime();
    }
}